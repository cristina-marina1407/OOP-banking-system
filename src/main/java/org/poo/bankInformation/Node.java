package org.poo.bankInformation;

public class Node {
    private String to;
    private double rate;

    public Node(final String to, final double rate) {
        this.to = to;
        this.rate = rate;
    }

    /**
     * @return the currency to convert to
     */
    public String getTo() {
        return to;
    }

    /**
     * @param to the currency to convert to
     */
    public void setTo(final String to) {
        this.to = to;
    }

    /**
     * @return the rate
     */
    public double getRate() {
        return rate;
    }

    /**
     * @param rate the rate to set
     */
    public void setRate(final double rate) {
        this.rate = rate;
    }
}
