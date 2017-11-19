package lk.ac.mrt.cse.dbs.simpleexpensemanager.data.impl;

/**
 * Created by cybx_live on 11/19/17.
 */

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.TransactionDAO;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.Account;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.ExpenseType;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.Transaction;


public class InDatabaseTransactionDAO implements TransactionDAO {

    AccntDetails mydbHelper;

    public InDatabaseTransactionDAO(Context context){
        mydbHelper = new AccntDetails(context);
    }

    @Override
    public void logTransaction(Date date, String accountNo, ExpenseType expenseType, double amount) {

        SQLiteDatabase db = mydbHelper.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put("accountNo",accountNo);
        values.put("date", date.toString());
        values.put("expenceType",expenseType.toString());
        values.put("amount",amount);

// Insert the new row, returning the primary key value of the new row
        long newRowId = db.insert("Transactions", null, values);
    }

    @Override
    public List<Transaction> getAllTransactionLogs(){

        SQLiteDatabase db = mydbHelper.getReadableDatabase();
        String [] columnX = {"date","accountNo", "expenceType","amount"};
        Cursor cursor = db.query("Transactions", columnX,
                null,null,null,null,null);

        List transactions = new ArrayList<>();

        while(cursor.moveToNext()) {

            String date = cursor.getString(cursor.getColumnIndexOrThrow("date"));
            String accountNo = cursor.getString(cursor.getColumnIndexOrThrow("accountNo"));
            String expenceType = cursor.getString(cursor.getColumnIndexOrThrow("expenceType"));
            double amount = cursor.getDouble(cursor.getColumnIndexOrThrow("amount"));

            ExpenseType x = null;

            if(expenceType.equals("EXPENSE")){
                x = ExpenseType.EXPENSE;
            }
            else{
                x = ExpenseType.INCOME;
            }

            Date date1= null;
            try {
                date1 = new SimpleDateFormat("EEE MMM d HH:mm:ss zzz yyyy").parse(date);
            } catch (ParseException e) {
                e.printStackTrace();
            }

            Transaction myacc = new Transaction(date1, accountNo, x, amount);
            transactions.add(myacc);

        }
        cursor.close();
        return transactions;
    }

    @Override
    public List<Transaction> getPaginatedTransactionLogs(int limit){
        SQLiteDatabase db = mydbHelper.getReadableDatabase();
        long cnt  = DatabaseUtils.queryNumEntries(db, "Transactions");

        if(limit<=cnt){
            return getAllTransactionLogs();
        }
        else{
            String [] columnX = {"date","accountNo", "expenceType","amount"};
            Cursor cursor = db.query("Transactions", columnX,
                    null,null,null,null,null);

            List transactions = new ArrayList<>();
            int count = 0;

            while(cursor.moveToNext() && count< limit) {

                String date = cursor.getString(cursor.getColumnIndexOrThrow("date"));
                String accountNo = cursor.getString(cursor.getColumnIndexOrThrow("accountNo"));
                String expenceType = cursor.getString(cursor.getColumnIndexOrThrow("expenceType"));
                double amount = cursor.getDouble(cursor.getColumnIndexOrThrow("amount"));

                ExpenseType x = null;

                if(expenceType.equals("EXPENSE")){
                    x = ExpenseType.EXPENSE;
                }
                else{
                    x = ExpenseType.INCOME;
                }

                Date date1= null;
                try {
                    date1 = new SimpleDateFormat("EEE MMM d HH:mm:ss zzz yyyy").parse(date);

                } catch (ParseException e) {

                    e.printStackTrace();
                }

                Transaction myacc = new Transaction(date1, accountNo, x, amount);
                transactions.add(myacc);
                count++;
            }

            cursor.close();
            return transactions;
        }
    }
}
