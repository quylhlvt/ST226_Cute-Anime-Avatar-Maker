package com.create.avatar.maker.ocdesgin.ui.setting

import android.view.View
import com.create.avatar.maker.ocdesgin.base.AbsBaseActivity
import com.create.avatar.maker.ocdesgin.ui.language.LanguageActivity
import com.create.avatar.maker.ocdesgin.utils.RATE
import com.create.avatar.maker.ocdesgin.utils.SharedPreferenceUtils
import com.create.avatar.maker.ocdesgin.utils.newIntent
import com.create.avatar.maker.ocdesgin.utils.onSingleClick
import com.create.avatar.maker.ocdesgin.utils.policy
import com.create.avatar.maker.ocdesgin.utils.rateUs
import com.create.avatar.maker.ocdesgin.utils.shareApp
import com.create.avatar.maker.ocdesgin.utils.unItem
import com.create.avatar.maker.ocdesgin.R
import com.create.avatar.maker.ocdesgin.databinding.ActivitySettingBinding
import com.create.avatar.maker.ocdesgin.utils.show
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class SettingActivity : AbsBaseActivity<ActivitySettingBinding>() {
    @Inject
    lateinit var sharedPreferences: SharedPreferenceUtils
    override fun getLayoutId(): Int = R.layout.activity_setting

    override fun initView() {

        if (sharedPreferences.getBooleanValue(RATE)) {
            binding.llRateUs.visibility = View.GONE
        }
        unItem = {
            binding.llRateUs.visibility = View.GONE
        }
    }

    override fun onStop() {
        super.onStop()
    }
    override fun initAction() {
        binding.apply {
            llLanguage.onSingleClick {
                startActivity(
                    newIntent(
                        applicationContext,
                        LanguageActivity::class.java
                    )
                )
            }
//            imvMusic.onSingleClick {
//                initMusic(imvMusic)
//            }
            llRateUs.onSingleClick {
                rateUs(0)
            }
            llShareApp.onSingleClick {
                shareApp()
            }
            llPrivacy.onSingleClick {
                policy()
            }
            binding.imvBack.onSingleClick {
                finish()
            }
        }
    }
}