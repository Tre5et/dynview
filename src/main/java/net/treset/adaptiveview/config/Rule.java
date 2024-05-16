package net.treset.adaptiveview.config;

public class Rule {
    private RuleType type;
    private String value;
    private Integer max;
    private Integer min;
    private Integer updateRate;
    private Integer step;
    private Integer maxViewDistance;
    private Integer minViewDistance;
    private transient boolean valid = true;

    public Rule(RuleType type, String value, Integer max, Integer min, Integer updateRate, Integer step, Integer maxViewDistance, Integer minViewDistance) {
        this.type = type;
        this.value = value;
        this.max = max;
        this.min = min;
        this.updateRate = updateRate;
        this.step = step;
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
                if(value != null && serverState.getPlayers().stream().map(String::toLowerCase).toList().contains(value.toLowerCase())) {
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
            sb.append(min).append(" <= ").append(type.toString().toLowerCase()).append(" <= ").append(max);
        } else if(min != null) {
            sb.append(type.toString().toLowerCase()).append(" >= ").append(min);
        } else if(max != null) {
            sb.append(type.toString().toLowerCase()).append(" <= ").append(max);
        } else if(value != null) {
            sb.append(type.toString().toLowerCase()).append(" = ").append(value);
        }
        return sb.toString();
    }

    public String toActionString() {
        StringBuilder sb = new StringBuilder();
        int len = 0;
        if(updateRate != null) {
            sb.append("update_rate = ").append(updateRate);
            len++;
        }
        if(step != null) {
            if(len > 0) {
                sb.append(", ");
            }
            sb.append("step = ").append(step);
            len++;
        }
        if(maxViewDistance != null) {
            if(len > 0) {
                sb.append(", ");
            }
            sb.append("max_view_distance = ").append(maxViewDistance);
            len++;
        }
        if(minViewDistance != null) {
            if(len > 0) {
                sb.append(", ");
            }
            sb.append("min_view_distance = ").append(minViewDistance);
            len++;
        }
        if(len == 0) {
            sb.append("no effect");
        }
        return sb.toString();
    }

    @Override
    public String toString() {
        return  "Condition: " + toConditionString() + "; Action: " + toActionString();
    }

    public RuleType getType() {
        return type;
    }

    public void setType(RuleType type) {
        this.type = type;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public Integer getMax() {
        return max;
    }

    public void setMax(Integer max) {
        this.max = max;
    }

    public Integer getMin() {
        return min;
    }

    public void setMin(Integer min) {
        this.min = min;
    }

    public Integer getUpdateRate() {
        return updateRate;
    }

    public void setUpdateRate(Integer updateRate) {
        this.updateRate = updateRate;
    }

    public Integer getStep() {
        return step;
    }

    public void setStep(Integer step) {
        this.step = step;
    }

    public Integer getMaxViewDistance() {
        return maxViewDistance;
    }

    public void setMaxViewDistance(Integer maxViewDistance) {
        this.maxViewDistance = maxViewDistance;
    }

    public Integer getMinViewDistance() {
        return minViewDistance;
    }

    public void setMinViewDistance(Integer minViewDistance) {
        this.minViewDistance = minViewDistance;
    }
}
