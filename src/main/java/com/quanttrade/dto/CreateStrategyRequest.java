package com.quanttrade.dto;

import java.util.List;

public class CreateStrategyRequest {
    private String name;
    private String description;
    private String type;
    private List<IndicatorConfig> indicators;

    public static class IndicatorConfig {
        private String indicatorType;
        private Integer period1;
        private Integer period2;
        private Double threshold1;
        private Double threshold2;

        public String getIndicatorType() { return indicatorType; }
        public void setIndicatorType(String indicatorType) { this.indicatorType = indicatorType; }
        public Integer getPeriod1() { return period1; }
        public void setPeriod1(Integer period1) { this.period1 = period1; }
        public Integer getPeriod2() { return period2; }
        public void setPeriod2(Integer period2) { this.period2 = period2; }
        public Double getThreshold1() { return threshold1; }
        public void setThreshold1(Double threshold1) { this.threshold1 = threshold1; }
        public Double getThreshold2() { return threshold2; }
        public void setThreshold2(Double threshold2) { this.threshold2 = threshold2; }
    }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
    public List<IndicatorConfig> getIndicators() { return indicators; }
    public void setIndicators(List<IndicatorConfig> indicators) { this.indicators = indicators; }
}
