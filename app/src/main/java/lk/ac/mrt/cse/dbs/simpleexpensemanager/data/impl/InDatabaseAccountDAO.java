package lk.ac.mrt.cse.dbs.simpleexpensemanager.data.impl;

/**
 * Created by cybx_live on 11/19/17.
 */


        import android.app.Activity;
        import android.content.ContentValues;
        import android.content.Context;
        import android.database.Cursor;
        import android.database.sqlite.SQLiteCursorDriver;
        import android.database.sqlite.SQLiteDatabase;
        import android.database.sqlite.SQLiteQuery;

        import java.util.ArrayList;
        import java.util.List;

        import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.AccountDAO;
        import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.exception.InvalidAccountException;
        import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.Account;
        import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.ExpenseType;

public class InDatabaseAccountDAO implements AccountDAO {

    AccntDetails mydbHelper;



    public InDatabaseAccountDAO(Context context){

        mydbHelper = new AccntDetails(context);
    }


    @Override
    public List<String> getAccountNumbersList() {

        SQLiteDatabase db = mydbHelper.getReadableDatabase();
        String [] columnX = {"accountNo"};
        Cursor cursor = db.query("Accounts", columnX,
                null,null,null,null,null);
        List accountNOs = new ArrayList<>();

        while(cursor.moveToNext()) {
            String itemId = cursor.getString(
                    cursor.getColumnIndexOrThrow("accountNo"));
            accountNOs.add(itemId);
        }

        cursor.close();
        return accountNOs;
    }

    @Override
    public List<Account> getAccountsList() {
        SQLiteDatabase db = mydbHelper.getReadableDatabase();
        String [] columnX = {"accountNo","bankName", "accountHolderName","balance"};
        Cursor cursor = db.query("Accounts", columnX,
                null,null,null,null,null);

        List accounts = new ArrayList<>();

        while(cursor.moveToNext()) {

            String accNo = cursor.getString(cursor.getColumnIndexOrThrow("accountNo"));
            String bankName = cursor.getString(cursor.getColumnIndexOrThrow("bankName"));
            String accHolderName = cursor.getString(cursor.getColumnIndexOrThrow("accountHolderName"));
            double balance = cursor.getDouble(cursor.getColumnIndexOrThrow("balance"));

            Account myacc = new Account(accNo, bankName, accHolderName, balance);

            accounts.add(myacc);
        }
        cursor.close();
        return accounts;
    }

    @Override
    public Account getAccount(String accountNo) throws InvalidAccountException {

        SQLiteDatabase db = mydbHelper.getReadableDatabase();
        String [] columnX = {"accountNo","bankName", "accountHolderName","balance"};
        String[] arg = {accountNo};
        Cursor cursor = db.query("Accounts", columnX,
                "accountNo = ?",arg,null,null,null);

        Account myacc = null;

        while(cursor.moveToNext()) {

            String accNo = cursor.getString(cursor.getColumnIndexOrThrow("accountNo"));
            String bankName = cursor.getString(cursor.getColumnIndexOrThrow("bankName"));
            String accHolderName = cursor.getString(cursor.getColumnIndexOrThrow("accountHolderName"));
            double balance = cursor.getDouble(cursor.getColumnIndexOrThrow("balance"));

            myacc = new Account(accNo, bankName, accHolderName, balance);
        }

        cursor.close();
        return myacc;
    }

    @Override
    public void addAccount(Account account) {

        SQLiteDatabase db = mydbHelper.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put("accountNo",account.getAccountNo());
        values.put("bankName",account.getBankName());
        values.put("accountHolderName",account.getAccountHolderName());
        values.put("balance",account.getBalance());

// Insert the new row, returning the primary key value of the new row
        long newRowId = db.insert("Accounts", null, values);
    }

    @Override
    public void removeAccount(String accountNo) throws InvalidAccountException {

        SQLiteDatabase db = mydbHelper.getWritableDatabase();
        // Define 'where' part of query.
        String selection = "accountNo = ?";
// Specify arguments in placeholder order.
        String[] selectionArgs = { accountNo };
// Issue SQL statement.
        db.delete("Accounts", selection, selectionArgs);

    }

    @Override
    public void updateBalance(String accountNo, ExpenseType expenseType, double amount) throws InvalidAccountException {

        SQLiteDatabase db = mydbHelper.getWritableDatabase();
        Account tempAcc = getAccount(accountNo);

        double value = 0;

        switch (expenseType) {
            case EXPENSE:
                value = tempAcc.getBalance() - amount;
                break;
            case INCOME:
                value = tempAcc.getBalance() + amount;
                break;
        }

        // New value for one column
        ContentValues values = new ContentValues();
        values.put("balance" , value );

// Which row to update, based on the title
        String selection = "accountNo = ?";
        String[] selectionArgs = { accountNo };

        int count = db.update(
                "Accounts",
                values,
                selection,
                selectionArgs);

    }
}