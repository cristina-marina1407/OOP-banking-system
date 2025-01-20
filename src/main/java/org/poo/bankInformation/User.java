package org.poo.bankInformation;

import org.poo.fileio.UserInput;

import java.time.LocalDate;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import static org.poo.commands.helperMethods.Constants.STANDARD_COMMISSION_RATE;
import static org.poo.commands.helperMethods.Constants.SILVER_COMMISSION_RATE;
import static org.poo.commands.helperMethods.Constants.SILVER_THRESHOLD_RON;

public class User {
    private String firstName;
    private String lastName;
    private String email;
    private String birthDate;
    private String occupation;
    private String servicePlan;
    private List<Account> accounts;

    public User(final UserInput userInput) {
        this.firstName = userInput.getFirstName();
        this.lastName = userInput.getLastName();
        this.email = userInput.getEmail();
        this.birthDate = userInput.getBirthDate();
        this.occupation = userInput.getOccupation();
        if (this.occupation.equals("student")) {
            this.servicePlan = "student";
        } else {
            this.servicePlan = "standard";
        }
        this.accounts = new ArrayList<>();
    }

    /**
     * @return the user's age
     */
    public int calculateAge() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate birthday = LocalDate.parse(this.birthDate, formatter);
        LocalDate currentDate = LocalDate.now();
        return Period.between(birthday, currentDate).getYears();
    }

    /**
     * @param amount the amount to pay/withdraw/transfer
     * @param graph the graph used for currency conversion
     * @param account the account used for the operation
     * @return the commission for the operation
     */
    public double calculateCommission(final double amount, final Graph graph,
                                      final Account account) {
        double ronAmount = graph.convert(account.getCurrency(), "RON", amount);
        if (this.servicePlan.equals("standard")) {
            return amount * STANDARD_COMMISSION_RATE;
        } else if (this.servicePlan.equals("silver") && ronAmount >= SILVER_THRESHOLD_RON) {
            return amount * SILVER_COMMISSION_RATE;
        }
        return 0;
    }

    /**
     * @return the firstName
     */
    public String getFirstName() {
        return firstName;
    }

    /**
     * @param firstName the firstName to set
     */
    public void setFirstName(final String firstName) {
        this.firstName = firstName;
    }

    /**
     * @return the lastName
     */
    public String getLastName() {
        return lastName;
    }

    /**
     * @param lastName the lastName to set
     */
    public void setLastName(final String lastName) {
        this.lastName = lastName;
    }

    /**
     * @return the email
     */
    public String getEmail() {
        return email;
    }

    /**
     * @param email the email to set
     */
    public void setEmail(final String email) {
        this.email = email;
    }

    /**
     * @return the accounts
     */
    public List<Account> getAccounts() {
        return accounts;
    }

    /**
     * @param accounts the accounts to set
     */
    public void setAccounts(final List<Account> accounts) {
        this.accounts = accounts;
    }

    /**
     * @return the birthDate
     */
    public String getBirthDate() {
        return birthDate;
    }

    /**
     * @param birthDate the birthDate to set
     */
    public void setBirthDate(final String birthDate) {
        this.birthDate = birthDate;
    }

    /**
     * @return the occupation
     */
    public String getOccupation() {
        return occupation;
    }

    /**
     * @param occupation the occupation to set
     */
    public void setOccupation(final String occupation) {
        this.occupation = occupation;
    }

    /**
     * @return the servicePlan
     */
    public String getServicePlan() {
        return servicePlan;
    }

    /**
     * @param servicePlan the servicePlan to set
     */
    public void setServicePlan(final String servicePlan) {
        this.servicePlan = servicePlan;
    }
}
