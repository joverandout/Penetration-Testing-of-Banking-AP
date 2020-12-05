package wondough.tests;

import java.sql.*;

import wondough.*;
import static wondough.SessionUtil.*;

public class test3 {

	public void test() {
        System.out.println();
        String result = checkAgainstJavascriptInjection(); 

        System.out.print("Vulnerability 3:\t\t");
        if(result.equals("PASSED")) System.out.println((char)27 + "[32m" + result);
        else  System.out.println((char)27 + "[31m" + result);
        System.out.print((char)27 + "[30m"); 
    }

    public String checkAgainstJavascriptInjection(){     
        try{
        DbConnection db = Program.getInstance().getDbConnection(); //connect to the database
        db.createTransaction(1, 0, "<script>testScript</script>", 1); //create a new transaction 
        Transactions transactions = db.getTransactions(1); //get all the transactions for that user

        Transaction transactionJustEntered = transactions.getTransactions().get(0); //get the transaction just entered

        if(transactionJustEntered.getDescription().contains("<")) return "FAILED"; //if it contains < encoding hasn't worked
        else if(transactionJustEntered.getDescription().contains(">")) return "FAILED"; //if it contains > encoding hasn't worked
        else return "PASSED"; // otherwise encoding has worked
        }
        catch(SQLException e){
            System.out.println(e.toString());
        }

        return "FAILED";

        
    }
}

//REGEX-BASED STRNG SAITISATION

