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

	/*
	 * Create a database
	 */
	public Database createDatabase() throws SQLException {
		try {
			this.dbHelper.createDataBase();
		} catch (IOException e) {
			throw new Error("UnableToCreateDatabase");
		}
		return this;
	}

	/*
	 * Open a database
	 */
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

	/*
	 * Close a database
	 */
	public void close() {
		this.dbHelper.close();
	}

	/*
	 * Get rows in the database
	 */
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

	/*
	 * Update an entry
	 */
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

	/*
	 * Add a new entry
	 */
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

	/*
	 * Delete an entry
	 */
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

	/*
	 * Delete all entries
	 */
	public void deleteAllEntries() {
		this.db.delete(tableName, null, null);
	}

	/*
	 * Check the state of an entry
	 */
	public boolean checkState(Calendar cal) {
		boolean state = false;
		int hour = cal.get(Calendar.HOUR_OF_DAY);
		int minute = cal.get(Calendar.MINUTE);
		int month = cal.get(Calendar.MONTH);
		int day = cal.get(Calendar.DAY_OF_MONTH);
		int year = cal.get(Calendar.YEAR);
		String sql = "SELECT * FROM " + tableName + " WHERE hour = '" + hour
				+ "' AND minute = '" + minute + "' AND month = '" + month
				+ "' AND day = '" + day + "' AND year = '" + year + "'";
		Log.w("", sql);
		Cursor cursor = this.db.rawQuery(sql, null);
		if (cursor != null && cursor.getCount() == 1) {
			cursor.moveToNext();
			state = Boolean.parseBoolean(cursor.getString(5));
		}
		return state;
	}

	/*
	 * Helper class
	 */
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

		/*
		 * Create a database
		 */
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

		/*
		 * Check if a database exists
		 */
		private boolean checkDataBase() {
			File dbFile = new File(DB_PATH + databaseName);
			return dbFile.exists();
		}

		/*
		 * Copy a database
		 */
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

		/*
		 * Open a database
		 */
		private boolean openDataBase() throws SQLException {
			String mPath = this.DB_PATH + databaseName;

			this.mDataBase = SQLiteDatabase.openDatabase(mPath, null,
					SQLiteDatabase.OPEN_READWRITE);
			return this.mDataBase != null;
		}

		/*
		 * Close the database
		 */
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
