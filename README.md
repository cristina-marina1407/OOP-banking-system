# Banking System ##

## Description

- This project is a simplified version of a banking system that will
  simulate the basic functionalities of a bank. It handles various types of
  transactions between accounts, such as sending money, creating and deleting
  cards and managing account balances.
- The system operates through a series of commands, each with distinct
  properties and actions. These commands can be operations like transferring
  money, managing cards, or debugging by printing different aspects of the
  system.
- This phase of the project introduced the business account and a more advanced
  splitPayment command, the commission system and the cashback strategy,
  functionalities that bring this application closer to a real banking system.

## Implementation

The project implementation is centered around the `Command design pattern`, all
the operations specified in the input being handled by a class that implements
the Command interface. We can consider that the Application class, that handles
the input and the output, plays the role of the client in the structure of the
Command design pattern. In addition to using this design pattern for the
commands logic, the implementation also uses the `Factory design pattern` to
create the commands.

For this part of the project, there was added another design pattern to
handle the cashback logic, the `Strategy design pattern`. Furthermore, the
`Builder design pattern` was used to create the business account, design pattern
used for the accounts and transactions.

The classes used in this project can be divided into the following categories:

*Application class*:

- This class is responsible for reading the input, creating the necessary lists
  for the implementation and calling the methods that handle the commands.

*The bank information classes*:

- These classes are used to implement the bank facilities and the necessary
  objects to create them. First of all, we have the Account class that uses
  the Builder design pattern to create a classic account, a savings account
  or a business account. Every user has a list of accounts and every account
  has a list of cards.

- The business account brings a lot of functionalities along the way, such as
  having a list of associates and a business report. In order to create this
  report this type of account needs to have some data structures that keep
  track when an associate spends money at a commerciant, how much does an
  associate spends overall and how much an associate deposits in the account.
  Also, there were needed extra verifications when a payment was made to see if
  the user that made the payment is an associate of the business account.

- Another important class is the Graph class that is used to handle the exchange
  rates and the conversion of money between different currencies.

*The command classes*:

- These classes implement the Command interface and are used to handle the
  operations specified in the input. There are classes that take actions on
  accounts, cards, payments, reports and debugging. The CommandFactory class
  and the Command interface represent the logic for creating and executing the
  commands.
- For the operations on accounts, the commands are AddAccount, AddFunds,
  AdInterest, ChangeInterestRate, DeleteAccount, SetAlias, SetMinimumBalance.
  For the operations on cards, the commands are CreateCard, DeleteCard,
  CheckCardStatus.
- There are some helper classes that are used to find a user with a given
  email or to find an account by its iban and also to handle the parsing of
  the transactions.
- The paying commands implement the transfer between two accounts, the graph
  being used to convert from the currency of the sender to the currency of the
  receiver and the online payment, that offers the option to pay with a one time
  card that extends the Card class and uses a pay method to be able to reset the
  card number and create another one after the payment is done.
- An addition to the payment commands is the custom split payment that allows the
  user to split the payment between multiple accounts, that have to pay a
  different amount of money. In order to implement this command, there are
  created a few more classes that are used to store the information about the
  active split payments. A split payment is created when the command for it is
  executed, and it is added to the split payment manger that handles the list
  of split payments objects. A split payment can be finalized when all the
  users accept it or it can be canceled when an involved user rejects it.
- The report classes implement commands that print the transactions of an
  account in a given interval of time, the report shows all kinds of
  transactions and the spending report shows only the payments made online.
- The last category of commands is the debugging commands that print all the
  users and all the transactions.

*The cashback classes*:
- These classes are used to implement the cashback strategy. Every commerciant
  has a cashback strategy that is used to calculate the cashback for an account.
  Every account has a map that stores the number of transactions at every
  commerciant, a list with the active cashbacks and a variable that shows
  how much an account has spent in ron at a commerciant. There is used the
  strategy interface to calculete the cashback differently for the two possible
  strategy types, the nrOfTransactions strategy and the spendingThreshold
  strategy.

*The service plan classes*:
- Every user has a service plan that can be upgraded with a command or
automatically from silver to gold after a certain amount of money is spent.
Also, the service plan is used when calculating tha commission for a payment
or in the spending threshold strategy for the cashback.

*The transaction classes*:

- These classes are used to implement the transactions by using the Builder
  design pattern. Every command has a transaction that is created when the
  operation is executed, there can be errors or successful transactions.
  All of them are stored in a list of transactions for each account. Besides the
  transaction class itself, another class used for handling the transactions is
  the PrintTransactionsJson class that is used to print every type of transactions
  in a json format. All the transactions are stored in a list that is sorted by
  the timestamp, while this list is iterated every transaction is used to build
  the PrintTransactionsJson object and based on the type of the transaction the
  method that prints a specific transaction is called.






