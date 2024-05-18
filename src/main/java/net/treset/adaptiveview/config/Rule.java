package net.treset.adaptiveview.config;

import net.treset.adaptiveview.tools.TextTools;

public class Rule {
    private RuleType type;
    private String value;
    private Integer max;
    private Integer min;
    private Integer updateRate;
    private Integer step;
    private Integer stepAfter;
    private Integer maxViewDistance;
    private Integer minViewDistance;
    private transient boolean valid = true;
    private transient int counter = 0;

    public Rule(RuleType type, String value, Integer max, Integer min, Integer updateRate, Integer step, Integer stepAfter, Integer maxViewDistance, Integer minViewDistance) {
        this.type = type;
        this.value = value;
        this.max = max;
        this.min = min;
        this.updateRate = updateRate;
        this.step = step;
        this.stepAfter = stepAfter;
        this.maxViewDistance = maxViewDistance;
        this.minViewDistance = minViewDistance;
    }

    public boolean isEffective() {
        valid = isValid();
        return valid;
    }

    private boolean isValid() {
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
        return updateRate != null || step != null || maxViewDistance != null || minViewDistance != null;
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
                if(valueInMinMax(serverState.getMspt())) {
                    return true;
                }
            }
            case MEMORY -> {
                if(valueInMinMax(serverState.getMemory())) {
                    return true;
                }
            }
            case PLAYERS -> {
                if(value != null && TextTools.containsIgnoreCase(serverState.getPlayers(), value)) {
                    return true;
                } else if(valueInMinMax(serverState.getPlayers().size())) {
                    return true;
                }
            }
        }
        return false;
    }

    private boolean valueInMinMax(double value) {
        if(min != null) {
            return min <= value && (max == null || max >= value);
        } else return max != null && max >= value;
    }

    public String toConditionString() {
        StringBuilder sb = new StringBuilder();
        if(min != null && max != null) {
            sb.append("$b").append(min).append(" <= ").append(type.toString().toLowerCase()).append(" <= ").append(max).append("$b");
        } else if(min != null) {
            sb.append("$b").append(type.toString().toLowerCase()).append(" >= ").append(min).append("$b");
        } else if(max != null) {
            sb.append("$b").append(type.toString().toLowerCase()).append(" <= ").append(max).append("$b");
        } else if(value != null) {
            sb.append("$b").append(type.toString().toLowerCase()).append(" = ").append(value).append("$b");
        } else {
            sb.append("$b").append(type.toString().toLowerCase()).append("$b");
        }
        return sb.toString();
    }

    public String toActionString() {
        StringBuilder sb = new StringBuilder();
        int len = 0;
        if(updateRate != null) {
            sb.append("$bupdate_rate = ").append(updateRate).append("$b");
            len++;
        }
        if(step != null) {
            if(len > 0) {
                sb.append(", ");
            }
            sb.append("$bstep = ").append(step).append("$b");
            len++;

            if(stepAfter != null) {
                sb.append(", $bstep_after = ").append(stepAfter).append("$b");
            }
        }
        if(maxViewDistance != null) {
            if(len > 0) {
                sb.append(", ");
            }
            sb.append("$bmax_view_distance = ").append(maxViewDistance).append("$b");
            len++;
        }
        if(minViewDistance != null) {
            if(len > 0) {
                sb.append(", ");
            }
            sb.append("$bmin_view_distance = ").append(minViewDistance).append("$b");
            len++;
        }
        if(len == 0) {
            sb.append("$bno action").append("$b");
        }
        return sb.toString();
    }

    @Override
    public String toString() {
        String str = "Condition: " + toConditionString() + "; Action: " + toActionString();
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

    public Integer getMaxViewDistance() {
        return maxViewDistance;
    }

    public void setMaxViewDistance(Integer maxViewDistance) {
        this.maxViewDistance = maxViewDistance;
        isEffective();
    }

    public Integer getMinViewDistance() {
        return minViewDistance;
    }

    public void setMinViewDistance(Integer minViewDistance) {
        this.minViewDistance = minViewDistance;
        isEffective();
    }
}
