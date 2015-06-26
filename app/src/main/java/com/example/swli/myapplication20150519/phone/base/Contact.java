package com.example.swli.myapplication20150519.phone.base;

import android.app.Activity;
import android.content.ContentProviderOperation;
import android.database.Cursor;
import android.provider.ContactsContract;
import android.util.Log;
import android.util.Pair;

import com.example.swli.myapplication20150519.common.DBManager;
import com.example.swli.myapplication20150519.common.DateExt;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by swli on 6/24/2015.
 */
public class Contact {

    private Activity activity;
    private DBManager dbManager;

    public Contact(Activity activity)
    {
        this.activity = activity;
        this.dbManager = new DBManager(activity);
    }

    public String getContactID(String name) {
        String id = "0";
        Cursor cursor = activity.getContentResolver().query(
                android.provider.ContactsContract.Contacts.CONTENT_URI,
                new String[]{android.provider.ContactsContract.Contacts._ID},
                android.provider.ContactsContract.Contacts.DISPLAY_NAME +
                        "='" + name + "'", null, null);
        try {
            if (cursor.moveToNext()) {
                id = cursor.getString(cursor.getColumnIndex(
                        android.provider.ContactsContract.Contacts._ID));
            }
        } finally {
            cursor.close();
        }
        return id;
    }

    public String getContactRawID(String contactId)
    {
        long rawContactId = 0;
        String[] projection = new String[]{ContactsContract.CommonDataKinds.Event.RAW_CONTACT_ID};
        String selection = ContactsContract.CommonDataKinds.Event.CONTACT_ID + "=?";

        String[] selectionArgs = new String[]{
                String.valueOf(contactId)
        };

        Cursor c = activity.getContentResolver().query(ContactsContract.Data.CONTENT_URI,
                projection,
                selection,
                selectionArgs, null);
        try {
            if (c.moveToFirst()) {
                 rawContactId = c.getLong(0);
            }
        } finally {
            c.close();
        }

        return String.valueOf(rawContactId);
    }

    public HashMap<String,Pair<Integer,Integer>> getContactNameAndPhone()
    {
        HashMap<String,Pair<Integer,Integer>> list = new HashMap<String, Pair<Integer, Integer>>();
        String mimeType;
        int contactRawId;
        int oldContactRawId = -1;
        int contactId = -1;
        int oldrid = -1;
        String name = "";
        String mobile = "";
        Cursor cursor = activity.getContentResolver().query(ContactsContract.Data.CONTENT_URI, null, null, null, ContactsContract.Contacts.Data.RAW_CONTACT_ID);
        while (cursor.moveToNext()) {

            contactId = cursor.getInt(cursor.getColumnIndex(ContactsContract.Data.CONTACT_ID));
            contactRawId = cursor.getInt(cursor.getColumnIndex(ContactsContract.Contacts.Data.RAW_CONTACT_ID));

            if (oldrid != contactId) {
                if(!name.equals("") && !mobile.equals(""))
                {
                    String key = name +":"+mobile.replaceAll("\\s", "");
                    list.put(key, Pair.create(Integer.valueOf(oldrid), Integer.valueOf(oldContactRawId)));
                }
                oldrid = contactId;
                oldContactRawId = contactRawId;
            }

            // 取得mimetype类型
            mimeType = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.Data.MIMETYPE));
            // 获得通讯录中每个联系人的ID
            // 获得通讯录中联系人的名字
            if (ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE.equals(mimeType)) {
                String lasName = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.StructuredName.FAMILY_NAME));
                String firstName = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.StructuredName.GIVEN_NAME));
                name = lasName + firstName;
                if (lasName != null) {
                    name = lasName;
                }
                if (firstName != null) {
                    name = name + firstName;
                }
            }

            if (ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE.equals(mimeType)) {
                // 取出电话类型
                int phoneType = cursor.getInt(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.TYPE));
                if (phoneType == ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE) {
                     mobile = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                }
            }
        }
        return list;
    }

    public void updateContactBirthday(Pair<Integer,Integer> pairIds, DateExt birthday) {

        ArrayList<ContentProviderOperation> ops = new ArrayList<ContentProviderOperation>();
        try {

            String strBirthday = birthday.getFormatDateTime("yyyy-MM-dd");
            String contactID = pairIds.first.toString();

            String selectionExisted = ContactsContract.CommonDataKinds.Event.CONTACT_ID + "=? AND " +
                    ContactsContract.CommonDataKinds.Event.MIMETYPE + "=? AND " +
                    ContactsContract.CommonDataKinds.Event.TYPE + "=?";
            String[] selectionArgsExisted = new String[]{
                    String.valueOf(contactID),
                    String.valueOf(ContactsContract.CommonDataKinds.Event.CONTENT_ITEM_TYPE),
                    String.valueOf(ContactsContract.CommonDataKinds.Event.TYPE_BIRTHDAY)
            };

            Cursor cursor = activity.getContentResolver().query(ContactsContract.Data.CONTENT_URI, null, selectionExisted, selectionArgsExisted, null);

            if (cursor.moveToFirst()) {
                int index = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Event._ID);
                String eventId = cursor.getString(index);

                ops.add(ContentProviderOperation.newUpdate(ContactsContract.Data.CONTENT_URI)
                        .withSelection(ContactsContract.Data._ID + " = ?", new String[]{eventId})
                        .withValue(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.Event.CONTENT_ITEM_TYPE)
                        .withValue(ContactsContract.CommonDataKinds.Event.START_DATE, strBirthday)
                        .withValue(ContactsContract.CommonDataKinds.Event.TYPE, ContactsContract.CommonDataKinds.Event.TYPE_BIRTHDAY)
                        .build());
            } else {

//                long rawContactId = -1;
//                String[] projection = new String[]{ContactsContract.CommonDataKinds.Event.RAW_CONTACT_ID};
//                String selection = ContactsContract.CommonDataKinds.Event.CONTACT_ID + "=?";
//
//                String[] selectionArgs = new String[]{
//                        String.valueOf(contactID)
//                };
//
//                Cursor c2 = activity.getContentResolver().query(ContactsContract.Data.CONTENT_URI,
//                        projection,
//                        selection,
//                        selectionArgs, null);
//                try {
//                    if (c2.moveToFirst()) {
//                        rawContactId = c2.getLong(0);
//                    }
//                } finally {
//                    c2.close();
//                }

                ops.add(ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
                        .withValue(ContactsContract.Data.RAW_CONTACT_ID, pairIds.second.toString())
                        .withValue(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.Event.CONTENT_ITEM_TYPE)
                        .withValue(ContactsContract.CommonDataKinds.Event.START_DATE, strBirthday)
                        .withValue(ContactsContract.CommonDataKinds.Event.TYPE, ContactsContract.CommonDataKinds.Event.TYPE_BIRTHDAY)
                        .build());
            }
            cursor.close();

            activity.getContentResolver().applyBatch(ContactsContract.AUTHORITY, ops);
            Log.d("ContactsManager", "add contact success.");
        } catch (Exception e) {
            Log.d("ContactsManager", "add contact failed.");
            Log.e("ContactsManager", e.getMessage());
        }
    }

}
