package matheus.costa.shareit.sqlite;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import matheus.costa.shareit.objects.User;

/**
 * Created by Matheus on 02/12/2017.
 */

public class UserLocalSave extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "shareitlocal.sqlite";

    private static final int DATABASE_VERSION = 1;

    private final String CREATE_USER =
            "create table user (userUid text not null, userName text not null," +
            "userEmail text not null);";

    private final String DROP_USER = "drop table if exists user";



    public UserLocalSave(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }



    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_USER);
    }



    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onCreate(db);
    }



    public long saveUser(User user){
        SQLiteDatabase db = getWritableDatabase();

        ContentValues cv = new ContentValues();
        cv.put("userUid",user.getUserUid());
        cv.put("userName",user.getUserName());
        cv.put("userEmail",user.getUserEmail());

        return db.insert("user",null,cv);
    }



    public int removeUser(User user){
        SQLiteDatabase db = getWritableDatabase();
        String userUid = user.getUserUid();
        return db.delete("user","userUid=?",new String[]{userUid});
    }



    public User retrieveUser(){
        SQLiteDatabase db = getReadableDatabase();

        User user = new User();
        Cursor c = db.query("user",null,null,null,null,null,null);
        c.moveToFirst();

        while (!c.isAfterLast()){
            user.setUserUid(c.getString(0));
            user.setUserName(c.getString(1));
            user.setUserEmail(c.getString(2));
            c.moveToNext();
        }

        db.close();
        return user;
    }
}
