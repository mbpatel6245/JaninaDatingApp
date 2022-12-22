package com.jda.application.utils

import android.content.Context
import android.content.SharedPreferences


/**
 * Make Shared Preferences utility for saving local app fields.
 */

class SharedPreferencesUtility {
    companion object {
        private var mSharedPreferences: SharedPreferences? = null

        private fun initSharedPreferencesInstance(pContext: Context) {
            mSharedPreferences = pContext.getSharedPreferences(
                Constants.SharedPreferences.sSHARED_PREFERENCES,
                Context.MODE_PRIVATE
            )
        }

        private fun checkSharedPreferencesInstance(pContext: Context) {
            if (mSharedPreferences == null) {
                initSharedPreferencesInstance(pContext)
            }
        }


        /**
         * set data to sharedPreferences of any type
         * */

        fun setData(
            pContext: Context,
            pType: String,
            pSharedPrefString: String = Constants.sEmpty_String,
            pValue: Any
        ) {
            checkSharedPreferencesInstance(pContext)
            val editor = mSharedPreferences!!.edit()
            when (pType) {
                Constants.SharePreferencesEnum.StringType.name -> editor.putString(
                    pSharedPrefString,
                    pValue as String?
                )
                Constants.SharePreferencesEnum.IntType.name -> editor.putInt(
                    pSharedPrefString,
                    pValue as Int
                )
                Constants.SharePreferencesEnum.BooleanType.name -> editor.putBoolean(
                    pSharedPrefString,
                    pValue as Boolean
                )
                Constants.SharePreferencesEnum.FloatType.name -> editor.putFloat(
                    pSharedPrefString,
                    pValue as Float
                )
                Constants.SharePreferencesEnum.LongType.name -> editor.putLong(
                    pSharedPrefString,
                    pValue as Long
                )
            }
            editor.apply()
        }

        /**
         * get data from sharedPreferences of any type
         * */

        fun getData(
            pContext: Context,
            pType: String,
            pSharedPrefString: String = Constants.sEmpty_String
        ): Any {
            checkSharedPreferencesInstance(pContext)
            val data = Constants.sEmpty_String
            return when (pType) {
                Constants.SharePreferencesEnum.StringType.name -> mSharedPreferences!!.getString(
                    pSharedPrefString,
                    Constants.sEmpty_String
                )!!
                Constants.SharePreferencesEnum.IntType.name -> mSharedPreferences!!.getInt(
                    pSharedPrefString,
                    Constants.SharedPreferences.sDEFAULT_INT_VALUE
                )
                Constants.SharePreferencesEnum.BooleanType.name -> mSharedPreferences!!.getBoolean(
                    pSharedPrefString,
                    Constants.SharedPreferences.sDEFAULT_BOOLEAN_VALUE
                )
                Constants.SharePreferencesEnum.FloatType.name -> mSharedPreferences!!.getFloat(
                    pSharedPrefString,
                    Constants.SharedPreferences.sDEFAULT_FLOAT_VALUE
                )
                Constants.SharePreferencesEnum.LongType.name -> mSharedPreferences!!.getLong(
                    pSharedPrefString,
                    Constants.SharedPreferences.sDEFAULT_LONG_VALUE.toLong()
                )
                else -> data
            }
        }

        fun getLoggedState(pContext: Context): Boolean {
            return getData(
                pContext,
                Constants.SharePreferencesEnum.BooleanType.name,
                Constants.SharedPreferences.sLoggedState
            ) as Boolean
        }

        fun setLoggedState(pContext: Context) {
            setData(
                pContext,
                Constants.SharePreferencesEnum.BooleanType.name,
                Constants.SharedPreferences.sLoggedState,
                true
            )
        }
        fun setUserTermsAccepted(pContext: Context) {
            setData(
                    pContext,
                    Constants.SharePreferencesEnum.BooleanType.name,
                    Constants.SharedPreferences.sUserTermsAccepted,
                    true
            )
        }
        fun getUserTermsAccepted(pContext: Context): Boolean {
            return getData(
                    pContext,
                    Constants.SharePreferencesEnum.BooleanType.name,
                    Constants.SharedPreferences.sUserTermsAccepted
            ) as Boolean
        }

        fun setTokenInPreferences(pContext: Context, pToken: String) {
            setData(
                pContext,
                Constants.SharePreferencesEnum.StringType.name,
                Constants.SharedPreferences.sAccessToken,
                pToken
            )
        }

        /**
         * Clear all data from sharedPreferences
         * */
        fun clearAllData(pContext: Context) {
            checkSharedPreferencesInstance(pContext)
            mSharedPreferences!!.edit().clear().apply()
        }
    }
}