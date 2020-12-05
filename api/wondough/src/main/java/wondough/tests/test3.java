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
        DbConnection db = Program.getInstance().getDbConnection();
        db.createTransaction(1, 0, "<script>testScript</script>", 1);
        Transactions transactions = db.getTransactions(1);

        Transaction transactionJustEntered = transactions.getTransactions().get(0);

        if(transactionJustEntered.getDescription().contains("<")) return "FAILED";
        else if(transactionJustEntered.getDescription().contains(">")) return "FAILED";
        else return "PASSED";
        }
        catch(SQLException e){
            System.out.println(e.toString());
        }

        return "FAILED";

        
    }
}

//REGEX-BASED STRNG SAITISATION

