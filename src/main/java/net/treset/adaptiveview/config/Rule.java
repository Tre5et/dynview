package net.treset.adaptiveview.config;

import com.google.gson.annotations.SerializedName;
import net.treset.adaptiveview.tools.TextTools;

import java.util.Arrays;
import java.util.List;

public class Rule {
    private RuleType type;
    private String value;
    private Integer max;
    private Integer min;
    private RuleTarget target;
    private Integer updateRate;
    private Integer step;
    private Integer stepAfter;
    @SerializedName(value = "max_distance", alternate = "max_view_distance")
    private Integer maxDistance;
    @SerializedName(value = "min_distance", alternate = "min_view_distance")
    private Integer minDistance;
    private String name;
    private transient boolean valid = true;
    private transient int counter = 0;

    public Rule(RuleType type, String value, Integer max, Integer min, RuleTarget target, Integer updateRate, Integer step, Integer stepAfter, Integer maxDistance, Integer minDistance, String name) {
        this.type = type;
        this.value = value;
        this.max = max;
        this.min = min;
        this.target = target;
        this.updateRate = updateRate;
        this.step = step;
        this.stepAfter = stepAfter;
        this.maxDistance = maxDistance;
        this.minDistance = minDistance;
        this.name = name;

        setDefaults();
    }

    public boolean isEffective() {
        valid = isValid();
        return valid;
    }

    private boolean isValid() {
        setDefaults();

        switch(type) {
            case MSPT -> {
                if(!checkMinMaxValues(0, 1000)) {
                    return false;
                }
            }
            case MEMORY -> {
                if(!checkMinMaxValues(0, 100)) {
                    return false;
                }
            }
            case PLAYERS -> {
                if(value == null) {
                    if(!checkMinMaxValues(0, 500)) {
                        return false;
                    }
                } else {
                    if(min != null || max != null) {
                        return false;
                    }
                }
            }
        }
        return updateRate != null || step != null || maxDistance != null || minDistance != null;
    }

    private void setDefaults() {
        if(target == null) {
            target = RuleTarget.VIEW;
        }
    }

    private boolean checkMinMaxValues(int minAllowed, int maxAllowed) {
        if(min == null && max == null) {
            return false;
        }
        if(min != null && max != null && min > max) {
            return false;
        }
        if(min != null && (min < minAllowed || min > maxAllowed)) {
            return false;
        }
        if(max != null && (max < minAllowed || max > maxAllowed)) {
            return false;
        }
        return true;
    }

    public boolean applies(ServerState serverState) {
        if(!valid) return false;
        switch (type) {
            case MSPT -> {
                if(valueInMinMax(serverState.mspt())) {
                    return true;
                }
            }
            case MEMORY -> {
                if(valueInMinMax(serverState.memory())) {
                    return true;
                }
            }
            case PLAYERS -> {
                if(value != null) {
                    List<String> players = splitPlayers(value);

                    if(value.startsWith("&")) {
                        if(players.stream().allMatch((p) -> containsPlayer(serverState, p))) {
                            // All specified players are online
                            return true;
                        }
                    } else if(value.startsWith("!")) {
                        if(serverState.players().stream().anyMatch((p) -> !TextTools.containsIgnoreCase(players, p))) {
                            // Any not specified player is online
                            return true;
                        }
                    } else if(value.startsWith("\\")) {
                        if(players.stream().noneMatch((p) -> containsPlayer(serverState, p))) {
                            // None of the specified players are online
                            return true;
                        }
                    } else {
                        if(players.stream().anyMatch((p) -> containsPlayer(serverState, p))) {
                            // Any of the specified players are online
                            return true;
                        }
                    }
                } else if(valueInMinMax(serverState.players().size())) {
                    return true;
                }
            }
        }
        return false;
    }

    private List<String> splitPlayers(String value) {
        String players = value;
        if(players.startsWith("&") || players.startsWith("!") || players.startsWith("\\")) {
            players = players.substring(1);
        }

        String[] playerArray = players.split(",");
        return Arrays.stream(playerArray).map(String::trim).toList();
    }

    private boolean containsPlayer(ServerState serverState, String player) {
        return TextTools.containsIgnoreCase(serverState.players(), player);
    }

    private boolean valueInMinMax(double value) {
        if(min != null) {
            return min <= value && (max == null || max >= value);
        } else return max != null && max >= value;
    }

    public String toConditionString() {
        StringBuilder sb = new StringBuilder();
        if(min != null && max != null) {
            sb.append("$b").append(min).append(" <= ").append(type.getName()).append(" <= ").append(max).append("$b");
        } else if(min != null) {
            sb.append("$b").append(type.getName()).append(" >= ").append(min).append("$b");
        } else if(max != null) {
            sb.append("$b").append(type.getName()).append(" <= ").append(max).append("$b");
        } else if(value != null) {
            sb.append("$b").append(type.getName()).append(" = ").append(value).append("$b");
        } else {
            sb.append("$b").append(type.getName()).append("$b");
        }
        return sb.toString();
    }

    public String toActionString() {
        StringBuilder sb = new StringBuilder();
        sb.append("$b").append(target.getName()).append("$b").append(": ");
        int len = 0;
        if(updateRate != null) {
            sb.append("$bUpdate Rate = ").append(updateRate).append("$b");
            len++;
        }
        if(step != null) {
            if(len > 0) {
                sb.append(", ");
            }
            sb.append("$bStep = ").append(step).append("$b");
            len++;

            if(stepAfter != null) {
                sb.append(", $bStep After = ").append(stepAfter).append("$b");
            }
        }
        if(maxDistance != null) {
            if(len > 0) {
                sb.append(", ");
            }
            sb.append("$bMax = ").append(maxDistance).append("$b");
            len++;
        }
        if(minDistance != null) {
            if(len > 0) {
                sb.append(", ");
            }
            sb.append("$bMin = ").append(minDistance).append("$b");
            len++;
        }
        if(len == 0) {
            sb.append("$bno action").append("$b");
        }
        return sb.toString();
    }

    @Override
    public String toString() {
        String str = name == null ? "" : "$b" + name + "$b: ";
        str += "Condition: " + toConditionString() + "; Action: " + toActionString();
        if(!valid) {
            str += " $R($bIneffective!$b)$W";
        }
        return str;
    }

    public RuleType getType() {
        return type;
    }

    public void setType(RuleType type) {
        this.type = type;
        isEffective();
    }

    public RuleTarget getTarget() {
        return target;
    }

    public void setTarget(RuleTarget target) {
        this.target = target;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
        isEffective();
    }

    public Integer getMax() {
        return max;
    }

    public void setMax(Integer max) {
        this.max = max;
        isEffective();
    }

    public Integer getMin() {
        return min;
    }

    public void setMin(Integer min) {
        this.min = min;
        isEffective();
    }

    public Integer getUpdateRate() {
        return updateRate;
    }

    public void setUpdateRate(Integer updateRate) {
        this.updateRate = updateRate;
        isEffective();
    }

    public void incrementCounter() {
        counter++;
    }

    public Integer getStep() {
        if(stepAfter != null && counter % stepAfter != 0) return 0;
        return step;
    }

    public void setStep(Integer step) {
        this.step = step;
        isEffective();
    }

    public Integer getStepAfter() {
        return stepAfter;
    }

    public void setStepAfter(Integer stepAfter) {
        this.stepAfter = stepAfter;
        isEffective();
    }

    public Integer getMaxDistance() {
        return maxDistance;
    }

    public void setMaxDistance(Integer maxDistance) {
        this.maxDistance = maxDistance;
        isEffective();
    }

    public Integer getMinDistance() {
        return minDistance;
    }

    public void setMinDistance(Integer minDistance) {
        this.minDistance = minDistance;
        isEffective();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
