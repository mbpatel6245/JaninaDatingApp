package com.jda.application.utils

import android.os.Bundle
import com.jda.application.R
import com.jda.application.acivities.LoginActivity
import com.jda.application.base.activity.BaseActivity
import com.jda.application.base.fragment.BaseFragment

fun BaseActivity.loginReplace(fragment: BaseFragment) {
    this.supportFragmentManager.beginTransaction().setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_left, R.anim.enter_from_left, R.anim.exit_to_right).replace(R.id.loginContainerFL, fragment).commit()
}

fun BaseActivity.loginReplaceStack(fragment: BaseFragment) {
    this.supportFragmentManager.beginTransaction().setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_left, R.anim.enter_from_left, R.anim.exit_to_right).replace(R.id.loginContainerFL, fragment).addToBackStack(null).commit()
}
fun BaseActivity.loginAddStack(fragment: BaseFragment) {
    this.supportFragmentManager.beginTransaction().setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_left, R.anim.enter_from_left, R.anim.exit_to_right).add(R.id.loginContainerFL, fragment).addToBackStack(null).commit()
}

fun BaseActivity.loginReplaceStackBundle(fragment: BaseFragment, bundle: Bundle) {
    fragment.arguments = bundle
    this.supportFragmentManager.beginTransaction().setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_left, R.anim.enter_from_left, R.anim.exit_to_right).replace(R.id.loginContainerFL, fragment).addToBackStack(null).commit()
}

fun BaseActivity.popBackPress() {
    (this as LoginActivity).onBackPressed()
}

fun BaseActivity.mainReplace(fragment: BaseFragment) {
    this.supportFragmentManager.beginTransaction().setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_left, R.anim.enter_from_left, R.anim.exit_to_right).replace(R.id.mainContainerFL, fragment).commit()
}

fun BaseActivity.mainReplaceStack(fragment: BaseFragment) {
    this.supportFragmentManager.beginTransaction().setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_left, R.anim.enter_from_left, R.anim.exit_to_right).replace(R.id.mainContainerFL, fragment).addToBackStack(null).commit()
}

fun BaseActivity.mainReplaceStackId(fragment: BaseFragment,id:String) {
    this.supportFragmentManager.beginTransaction().setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_left, R.anim.enter_from_left, R.anim.exit_to_right).replace(R.id.mainContainerFL, fragment).addToBackStack(id).commit()
}

fun BaseActivity.mainReplaceStackBundle(fragment: BaseFragment, bundle: Bundle) {
    fragment.arguments = bundle
    this.supportFragmentManager.beginTransaction().setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_left, R.anim.enter_from_left, R.anim.exit_to_right).replace(R.id.mainContainerFL, fragment).addToBackStack(null).commit()
}

fun BaseActivity.mainReplaceStackBundleTag(fragment: BaseFragment, bundle: Bundle,tag:String) {
    fragment.arguments = bundle
    this.supportFragmentManager.beginTransaction().setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_left, R.anim.enter_from_left, R.anim.exit_to_right).replace(R.id.mainContainerFL, fragment).addToBackStack(tag).commit()
}

fun BaseActivity.mainAddStackBundleTag(fragment: BaseFragment, bundle: Bundle,tag:String) {
    fragment.arguments = bundle
    this.supportFragmentManager.beginTransaction().setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_left, R.anim.enter_from_left, R.anim.exit_to_right).add(R.id.mainContainerFL, fragment).addToBackStack(tag).commit()
}
