package com.create.avatar.maker.ocdesgin.ui.successcoslay

import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.bumptech.glide.Glide
import com.create.avatar.maker.ocdesgin.R
import com.create.avatar.maker.ocdesgin.base.AbsBaseActivity
import com.create.avatar.maker.ocdesgin.databinding.ActivitySuccessBinding
import com.create.avatar.maker.ocdesgin.databinding.ActivitySuccessCosplayBinding
import com.create.avatar.maker.ocdesgin.ui.main.MainActivity
import com.create.avatar.maker.ocdesgin.ui.my_creation.MyCreationActivity
import com.create.avatar.maker.ocdesgin.ui.permision.PermissionViewModel
import com.create.avatar.maker.ocdesgin.utils.CONST
import com.create.avatar.maker.ocdesgin.utils.PermissionHelper
import com.create.avatar.maker.ocdesgin.utils.SharedPreferenceUtils
import com.create.avatar.maker.ocdesgin.utils.newIntent
import com.create.avatar.maker.ocdesgin.utils.onClick
import com.create.avatar.maker.ocdesgin.utils.onSingleClick
import com.create.avatar.maker.ocdesgin.utils.saveFileToExternalStorage
import com.create.avatar.maker.ocdesgin.utils.scanMediaFile
import com.create.avatar.maker.ocdesgin.utils.shareListFiles
import com.create.avatar.maker.ocdesgin.utils.showDialogNotifiListener
import com.create.avatar.maker.ocdesgin.utils.showToast
import dagger.hilt.android.AndroidEntryPoint
import java.io.File
import javax.inject.Inject
import kotlin.getValue


@AndroidEntryPoint
class SuccessCosplayActivity : AbsBaseActivity<ActivitySuccessCosplayBinding>() {
    var path1 = ""
    var path2 = ""
    override fun getLayoutId(): Int = R.layout.activity_success_cosplay

    override fun initView() {
        val matchPercent = intent.getIntExtra("matchPercent", 0)
        path1 = intent.getStringExtra("cosplayBitmapPath").toString()
        path2 = intent.getStringExtra("currentBitmapPath").toString()

        Glide.with(applicationContext).load(path1).into(binding.imgCharacter1)
        Glide.with(applicationContext).load(path2).into(binding.imgCharacter2)

        binding.tvMatchPercent.text = "$matchPercent/100"

        // Luôn hiện 5 sao, fill theo phần trăm
        val starCount = (matchPercent / 20).coerceIn(0, 5)
        binding.ll1.rating = starCount.toFloat()

        binding.progressTrack.post {
            val trackW = binding.progressTrack.width.toFloat()
            val fillMarginStartPx = 18 * resources.displayMetrics.density
            val fillW = trackW - fillMarginStartPx
            val targetScale = matchPercent / 100f
            val adjustedScale = targetScale * fillW / trackW

            binding.progressFill.pivotX = 0f
            binding.progressFill.pivotY = binding.progressFill.height / 2f
            binding.progressFill.scaleX = adjustedScale
            binding.progressFill.scaleY = 1f

            val starW = binding.imgStar.width.toFloat()
            binding.imgStar.translationX = fillMarginStartPx + fillW * targetScale - starW / 2f
        }

        binding.tvTitle.isSelected = true
        binding.imvBack.isSelected = true
    }

    override fun initAction() {
        binding.apply {
            imvBack.onSingleClick { finish() }
            imvHome.onSingleClick {
                startActivity(newIntent(applicationContext, MainActivity::class.java))
                finish()
            }
        }
    }




}