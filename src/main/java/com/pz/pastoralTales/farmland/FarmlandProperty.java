package com.pz.pastoralTales.farmland;

public class FarmlandProperty {
    private double baseValue;
    private double minValue;
    private double maxValue;
    private double consumption;
    private Recovery recovery;
    private Modifiers modifiers;

    public double getBaseValue() {
        return baseValue;
    }

    public double getMinValue() {
        return minValue;
    }

    public double getMaxValue() {
        return maxValue;
    }

    public double getConsumption() {
        return consumption;
    }

    public Recovery getRecovery() {
        return recovery;
    }

    public Modifiers getModifiers() {
        return modifiers;
    }

    public static class Recovery {
        private double tickRate;
        private double amount;

        public double getTickRate() {
            return tickRate;
        }

        public double getAmount() {
            return amount;
        }
    }

    public static class Modifiers {
        private double T;
        private double V;
        private double C;
        private double E;
        private double D;
        private double W;

        public double getT() {
            return T;
        }

        public double getV() {
            return V;
        }

        public double getC() {
            return C;
        }

        public double getE() {
            return E;
        }

        public double getD() {
            return D;
        }

        public double getW() {
            return W;
        }
    }
}
