package com.team3.domore;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Calendar;
import android.annotation.SuppressLint;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

@SuppressLint("SdCardPath")
public class Database {
	private String databaseName;
	private String tableName;
	private SQLiteDatabase db;
	private myDBHelper dbHelper;

	public Database(Context context, String databaseName, String tableName) {
		this.databaseName = databaseName;
		this.tableName = tableName;
		this.dbHelper = new myDBHelper(context, databaseName, null, 1);
	}

	public Database createDatabase() throws SQLException {
		try {
			this.dbHelper.createDataBase();
		} catch (IOException e) {
			throw new Error("UnableToCreateDatabase");
		}
		return this;
	}

	public Database open() throws SQLException {
		try {
			this.dbHelper.openDataBase();
			this.dbHelper.close();
			this.db = this.dbHelper.getReadableDatabase();
		} catch (SQLException e) {
			throw e;
		}
		return this;
	}

	public void close() {
		this.dbHelper.close();
	}

	public Cursor getData() {
		try {
			String sql = "SELECT * FROM " + tableName;

			Cursor cursor = this.db.rawQuery(sql, null);
			if (cursor != null) {
				cursor.moveToNext();
			}
			return cursor;
		} catch (SQLException e) {
			throw e;
		}
	}

	public void update(Calendar cal, boolean newState) {
		String sql = "UPDATE " + tableName + " SET state = '" + newState
				+ "' WHERE hour = '" + cal.get(Calendar.HOUR_OF_DAY)
				+ "' AND minute = '" + cal.get(Calendar.MINUTE)
				+ "' AND month = '" + cal.get(Calendar.MONTH) + "' AND day = '"
				+ cal.get(Calendar.DAY_OF_MONTH) + "' AND year = '"
				+ cal.get(Calendar.YEAR) + "'";
		Log.w("", sql);
		this.db.execSQL(sql);
	}

	public boolean addEntry(Calendar cal, int id) {
		int hour = cal.get(Calendar.HOUR_OF_DAY);
		int minute = cal.get(Calendar.MINUTE);
		int month = cal.get(Calendar.MONTH);
		int day = cal.get(Calendar.DAY_OF_MONTH);
		int year = cal.get(Calendar.YEAR);
		String sql = "INSERT INTO " + tableName + " VALUES ('" + hour + "' , '"
				+ minute + "' , '" + month + "' , '" + day + "' , '" + year
				+ "' , 'true', " + id + ")";

		String sql2 = "SELECT * FROM " + tableName + " WHERE hour = '" + hour
				+ "' AND minute = '" + minute + "' AND month = '" + month
				+ "' AND day = '" + day + "' AND year = '" + year + "'";

		Log.w("", sql2);
		Cursor cursor = this.db.rawQuery(sql2, null);

		if (cursor.getCount() == 0) {
			Log.w("", sql);
			this.db.execSQL(sql);
			return true;
		}	
		return false;
	}

	public void deleteEntry(Calendar cal) {
		int hour = cal.get(Calendar.HOUR_OF_DAY);
		int minute = cal.get(Calendar.MINUTE);
		int month = cal.get(Calendar.MONTH);
		int day = cal.get(Calendar.DAY_OF_MONTH);
		int year = cal.get(Calendar.YEAR);
		String sql = "DELETE FROM " + tableName + " WHERE hour = '" + hour
				+ "' AND minute = '" + minute + "' AND month = '" + month
				+ "' AND day = '" + day + "' AND year = '" + year + "'";
		Log.w("", sql);
		this.db.execSQL(sql);
	}

	public Cursor search(String key) {
		try {
			String sql = "SELECT * FROM " + tableName + " WHERE _id LIKE '%"
					+ key + "%' OR address LIKE '%" + key + "%' OR tel LIKE '%"
					+ key + "%' OR intro LIKE '%" + key + "%'";

			Cursor cursor = this.db.rawQuery(sql, null);

			if (cursor != null) {
				cursor.moveToNext();
			}
			return cursor;
		} catch (SQLException e) {
			throw e;
		}
	}

	private class myDBHelper extends SQLiteOpenHelper {
		private String DB_PATH = "";
		private SQLiteDatabase mDataBase;
		private Context context;

		private myDBHelper(Context context, String name, CursorFactory factory,
				int version) {
			super(context, name, factory, version);
			this.DB_PATH = "/data/data/" + context.getPackageName()
					+ "/databases/";
			this.context = context;
		}

		private void createDataBase() throws IOException {
			if (!checkDataBase()) {
				this.getReadableDatabase();
				this.close();
				try {
					this.copyDataBase();
				} catch (IOException e) {
					throw new Error("ErrorCopyingDataBase");
				}
			}
		}

		private boolean checkDataBase() {
			File dbFile = new File(DB_PATH + databaseName);
			return dbFile.exists();
		}

		private void copyDataBase() throws IOException {
			InputStream mInput = this.context.getAssets().open(databaseName);
			String outFileName = this.DB_PATH + databaseName;
			OutputStream mOutput = new FileOutputStream(outFileName);
			byte[] mBuffer = new byte[1024];
			int mLength;
			while ((mLength = mInput.read(mBuffer)) > 0) {
				mOutput.write(mBuffer, 0, mLength);
			}
			mOutput.flush();
			mOutput.close();
			mInput.close();
		}

		private boolean openDataBase() throws SQLException {
			String mPath = this.DB_PATH + databaseName;

			this.mDataBase = SQLiteDatabase.openDatabase(mPath, null,
					SQLiteDatabase.OPEN_READWRITE);
			return this.mDataBase != null;
		}

		@Override
		public synchronized void close() {
			if (this.mDataBase != null)
				this.mDataBase.close();
			super.close();
		}

		@Override
		public void onCreate(SQLiteDatabase db) {
			// does nothing
		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			// does nothing
		}
	}

}
