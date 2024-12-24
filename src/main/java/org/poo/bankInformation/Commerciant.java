package org.poo.bankInformation;

import org.poo.fileio.CommerciantInput;

public class Commerciant {
    private String commerciant;
    private int id;
    private String account;
    private String type;
    private String cashbackStrategy;

    public Commerciant(final CommerciantInput commerciantInput) {
        this.id = commerciantInput.getId();
        this.commerciant = commerciantInput.getCommerciant();
        this.account = commerciantInput.getAccount();
        this.type = commerciantInput.getType();
        this.cashbackStrategy = commerciantInput.getCashbackStrategy();
    }

    /**
     * @return the id
     */
    public int getId() {
        return id;
    }

    /**
     * @param id the id to set
     */
    public void setId(final int id) {
        this.id = id;
    }

    /**
     * @return the commerciant
     */
    public String getCommerciant() {
        return commerciant;
    }

    /**
     * @param commerciant the commerciant to set
     */
    public void setCommerciant(final String commerciant) {
        this.commerciant = commerciant;
    }

    /**
     * @return the account
     */
    public String getAccount() {
        return account;
    }

    /**
     * @param account the account to set
     */
    public void setAccount(final String account) {
        this.account = account;
    }

    /**
     * @return the type
     */
    public String getType() {
        return type;
    }

    /**
     * @param type the type to set
     */
    public void setType(final String type) {
        this.type = type;
    }

    /**
     * @return the cashbackStrategy
     */
    public String getCashbackStrategy() {
        return cashbackStrategy;
    }

    /**
     * @param cashbackStrategy the cashbackStrategy to set
     */
    public void setCashbackStrategy(final String cashbackStrategy) {
        this.cashbackStrategy = cashbackStrategy;
    }
}
