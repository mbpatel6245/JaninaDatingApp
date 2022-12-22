package com.jda.application.utils

import android.app.Activity
import android.util.Patterns
import com.jda.application.R
import com.jda.application.utils.UserAlertUtility.Companion.showToast
import java.util.regex.Matcher
import java.util.regex.Pattern

object ValidationUtility {
    fun validatePreferences(
            pFirstName: String?,
            pLastName: String?,
            pGender: String?,
            pGenderChoice: ArrayList<Int>?,
            pAge: String?,
            pLocation: String?,
            pStatus: String?,
            pHeight: String?,
            pEthnicity: ArrayList<Int>?,
            pEthnicityChoice: ArrayList<Int>?,
            pBeliefList: String?,
            pBeliefChoiceList: ArrayList<Int>?,
            pContext: Activity,
            isImageUploaded:Boolean
    ): Boolean {
        when {
            pFirstName!!.isEmpty() -> {
                pContext.showToast(pContext.resources.getString(R.string.please_enter_first_name))
            }
            pLastName!!.isEmpty() -> {
                pContext.showToast(pContext.resources.getString(R.string.please_enter_last_name))
            }
//            !isStringOnlyAlphabet(pFirstName) -> {
//                pContext.showToast(pContext.resources.getString(R.string.name_only_contains_character))
//            }
//            !isStringOnlyAlphabet(pLastName) -> {
//                pContext.showToast(pContext.resources.getString(R.string.name_only_contains_character))
//            }
            pGender.isNullOrEmpty() -> {
                pContext.showToast(pContext.resources.getString(R.string.please_select_gender))
            }
            pGenderChoice.isNullOrEmpty() -> {
                pContext.showToast(pContext.resources.getString(R.string.please_select_gender_choice))
            }
            pAge.isNullOrEmpty() -> {
                pContext.showToast(pContext.resources.getString(R.string.please_select_age))
            }
            pLocation.isNullOrEmpty() -> {
                pContext.showToast(pContext.resources.getString(R.string.please_select_location))
            }
            pStatus.isNullOrEmpty() -> {
                pContext.showToast(pContext.resources.getString(R.string.please_select_martial_status))
            }
            pHeight.isNullOrEmpty() -> {
                pContext.showToast(pContext.resources.getString(R.string.please_select_height))
            }
            pEthnicity.isNullOrEmpty() -> {
                pContext.showToast(pContext.resources.getString(R.string.please_select_ethnicity))
            }
            pEthnicityChoice.isNullOrEmpty() -> {
                pContext.showToast(pContext.resources.getString(R.string.please_select_ethnicity_choice))
            }
            pBeliefList.isNullOrEmpty() -> {
                pContext.showToast(pContext.resources.getString(R.string.please_select_belief))
            }
            pBeliefChoiceList.isNullOrEmpty() -> {
                pContext.showToast(pContext.resources.getString(R.string.please_select_belief_choice))
            }
//            !isImageUploaded ->{
//                pContext.showToast(pContext.getString(R.string.please_upload_your_portfolio_image))
//            }
            else -> {
                return true
            }
        }
        return false
    }


    fun validatePreferences(
            pFirstName: String?,
            pLastName: String?,
            pGender: String?,
//            pGenderChoice: String?,
            pGenderChoice:MutableList<Int>?,
            pAge: String?,
            pStatus: String?,
            pHeight: String?,
            pEthnicity:MutableList<Int>?,
            pEthnicityChoice:MutableList<Int>?,
            pBeliefList: String?,
            pBeliefChoiceList: MutableList<Int>?,
            pContext: Activity
    ): Boolean {
        when {
            pFirstName!!.isEmpty() -> {
                pContext.showToast(pContext.resources.getString(R.string.please_enter_first_name))
            }
            pLastName!!.isEmpty() -> {
                pContext.showToast(pContext.resources.getString(R.string.please_enter_last_name))
            }
//            !isStringOnlyAlphabet(pFirstName) -> {
//                pContext.showToast(pContext.resources.getString(R.string.name_only_contains_character))
//            }
//            !isStringOnlyAlphabet(pLastName) -> {
//                pContext.showToast(pContext.resources.getString(R.string.name_only_contains_character))
//            }
            pGender.isNullOrEmpty() -> {
                pContext.showToast(pContext.resources.getString(R.string.please_select_gender))
            }
            pGenderChoice.isNullOrEmpty() -> {
                pContext.showToast(pContext.resources.getString(R.string.please_select_gender_choice))
            }
            pAge.isNullOrEmpty() -> {
                pContext.showToast(pContext.resources.getString(R.string.please_select_age))
            }
            pStatus.isNullOrEmpty() -> {
                pContext.showToast(pContext.resources.getString(R.string.please_select_martial_status))
            }
            pHeight.isNullOrEmpty() -> {
                pContext.showToast(pContext.resources.getString(R.string.please_select_height))
            }
            pEthnicity.isNullOrEmpty() -> {
                pContext.showToast(pContext.resources.getString(R.string.please_select_ethnicity))
            }
            pEthnicityChoice.isNullOrEmpty() -> {
                pContext.showToast(pContext.resources.getString(R.string.please_select_ethnicity_choice))
            }
            pBeliefList.isNullOrEmpty() -> {
                pContext.showToast(pContext.resources.getString(R.string.please_select_belief))
            }
            pBeliefChoiceList.isNullOrEmpty() -> {
                pContext.showToast(pContext.resources.getString(R.string.please_select_belief_choice))
            }
            else -> {
                return true
            }
        }
        return false
    }

    fun validateMinMax(pMin: String?, pMax: String?, pContext: Activity): Boolean {
        return if (pMin == pMax) {
            pContext.showToast(pContext.getString(R.string.min_age_max_not_equal))
            false
        } else {
            true
        }
    }


    fun validateEmail(pEmail: String, pContext: Activity): Boolean {
        if (pEmail.isEmpty()) {
            UserAlertUtility.showToast(
                    pContext.resources.getString(R.string.please_enter_email),
                    pContext
            )
        } else if (!isValidEmail(pEmail)) {
            UserAlertUtility.showToast(
                    pContext.resources.getString(R.string.enter_valid_email),
                    pContext
            )
        } else {
            return true
        }
        return false
    }

    fun validatePhoneNumber(
            pCountryCode: String,
            pPhoneNumber: String,
            pContext: Activity
    ): Boolean {
        when {
            pCountryCode.isEmpty() -> {
                UserAlertUtility.showToast(
                        pContext.resources.getString(R.string.please_enter_code),
                        pContext
                )
            }
            pPhoneNumber.isEmpty() -> {
                UserAlertUtility.showToast(
                        pContext.resources.getString(R.string.please_enter_phone_number),
                        pContext
                )
            }
            else -> {
                return true
            }
        }
        return false
    }

    fun validatePassword(
            pOldPassword: String,
            pNewPassword: String,
            pConfirmPassword: String,
            pContext: Activity
    ): Boolean {
        when {
            pOldPassword.isEmpty() -> {
                UserAlertUtility.showToast(
                        pContext.resources.getString(R.string.please_enter_old_password),
                        pContext
                )
            }
            pOldPassword.length < 8 -> {
                UserAlertUtility.showToast(
                        pContext.resources.getString(R.string.old_password_length_msg),
                        pContext
                )
            }
            pNewPassword.isEmpty() -> {
                UserAlertUtility.showToast(
                        pContext.resources.getString(R.string.please_enter_new_password),
                        pContext
                )
            }

            pNewPassword.length < 8 -> {
                UserAlertUtility.showToast(
                        pContext.resources.getString(R.string.new_password_length_msg),
                        pContext
                )
            }
            pConfirmPassword.isEmpty() -> {
                UserAlertUtility.showToast(
                        pContext.resources.getString(R.string.please_enter_confirm_password),
                        pContext
                )
            }
            pConfirmPassword.length < 8 -> {
                UserAlertUtility.showToast(
                        pContext.resources.getString(R.string.confirm_password_length_msg),
                        pContext
                )
            }
            else -> {
                return true
            }
        }
        return false
    }

    private fun isValidPassword(password: String?): Boolean {
        val matcher: Matcher
        val pattern: Pattern = Pattern.compile(Constants.Regex.sPasswordPattern)
        matcher = pattern.matcher(password!!)
        return matcher.matches()
    }

    private fun isValidEmail(email: String): Boolean {
        val matcher = Patterns.EMAIL_ADDRESS.matcher(email)
        return matcher.matches()
    }


    private fun isStringOnlyAlphabet(str: String?): Boolean {
        return (str != null
                && str != ""
                && str.matches("^[\\p{L} .'-]+$".toRegex()))
    }

    fun validateConfirmPassword(
            pPassword: String,
            pConfirmPassword: String,
            pContext: Activity?
    ): Boolean {
        if (pPassword != pConfirmPassword) {
            UserAlertUtility.showToast(
                    pContext?.resources?.getString(R.string.password_mismatch),
                    pContext
            )
            return false
        }
        return true
    }
}