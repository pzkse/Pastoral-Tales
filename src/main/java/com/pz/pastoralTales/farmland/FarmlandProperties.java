package com.pz.pastoralTales.farmland;

import java.util.Map;

public class FarmlandProperties {
    private Map<String, FarmlandProperty> properties;

    public Map<String, FarmlandProperty> getProperties() {
        return properties;
    }

    public FarmlandProperty getProperty(String name) {
        return properties.get(name);
    }
}