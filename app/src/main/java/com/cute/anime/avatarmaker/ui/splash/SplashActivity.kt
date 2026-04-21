package com.cute.anime.avatarmaker.ui.splash

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ValueAnimator
import android.os.Bundle
import android.view.animation.DecelerateInterpolator
import androidx.lifecycle.lifecycleScope
import com.cute.anime.avatarmaker.R
import com.cute.anime.avatarmaker.base.AbsBaseActivity
import com.cute.anime.avatarmaker.data.callapi.reponse.LoadingStatus
import com.cute.anime.avatarmaker.data.repository.ApiRepository
import com.cute.anime.avatarmaker.databinding.ActivitySplashBinding
import com.cute.anime.avatarmaker.utils.DataHelper
import com.cute.anime.avatarmaker.utils.DataHelper.getData
import com.cute.anime.avatarmaker.utils.SharedPreferenceUtils
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class SplashActivity : AbsBaseActivity<ActivitySplashBinding>() {

    @Inject lateinit var apiRepository: ApiRepository
    @Inject lateinit var sharedPreferenceUtils: SharedPreferenceUtils

    private var minDelayPassed = false
    private var dataReady = false

    // Animator cho progress overlay
    private var progressAnimator: ValueAnimator? = null
    // Width hiện tại của overlay (tính theo tỉ lệ 0f..1f, 1f = che full = 0% progress)
    private var currentOverlayFraction = 1f

    override fun getLayoutId(): Int = R.layout.activity_splash

    override fun initView() {


        startFakeLoadingProgress()

        observeDataLoading()


                lifecycleScope.launch {
                    minDelayPassed = true
                    if (dataReady) navigateToNextScreen()
                }



    }

    override fun initAction() {
        lifecycleScope.launch(Dispatchers.IO) {
            getData(apiRepository)
        }
    }

    // ✅ Animate progress giả: overlay từ full → 80% rồi dừng chờ data
    private fun startFakeLoadingProgress() {
        binding.progressWrapper.post {
            animateOverlayTo(targetFraction = 0.2f, duration = 2500) {
                // Dừng ở 80% load, chờ data thật
            }
        }
    }

    // ✅ Khi data xong: animate nốt từ vị trí hiện tại → 0% (hết overlay)
    private fun completeProgress(onDone: () -> Unit) {
        animateOverlayTo(targetFraction = 0f, duration = 600) {
            onDone()
        }
    }

    /**
     * Animate width của progressOverlay
     * @param targetFraction 0f = không còn overlay (100% progress), 1f = che full (0% progress)
     */
    private fun animateOverlayTo(targetFraction: Float, duration: Long, onEnd: (() -> Unit)? = null) {
        progressAnimator?.cancel()

        val overlay = binding.progressOverlay
        val container = binding.progressWrapper

        val startFraction = currentOverlayFraction

        progressAnimator = ValueAnimator.ofFloat(startFraction, targetFraction).apply {
            this.duration = duration
            interpolator = DecelerateInterpolator()
            addUpdateListener { anim ->
                val fraction = anim.animatedValue as Float
                currentOverlayFraction = fraction
                val containerWidth = container.width - container.paddingLeft - container.paddingRight - 6 // trừ margin 3dp * 2
                val newWidth = (containerWidth * fraction).toInt()
                val lp = overlay.layoutParams
                lp.width = newWidth.coerceAtLeast(0)
                overlay.layoutParams = lp
            }
            addListener(object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator) {
                    onEnd?.invoke()
                }
            })
            start()
        }
    }

    private fun observeDataLoading() {
        DataHelper.arrDataOnline.observe(this) { response ->
            response?.let {
                when (it.loadingStatus) {
                   LoadingStatus.Success -> {
                        // Xử lý data ... (giữ nguyên logic cũ của bạn)

                        dataReady = true
                        // ✅ Animate nốt progress → 100% rồi mới navigate
                        completeProgress {
                            if (minDelayPassed) navigateToNextScreen()
                        }
                    }
                    LoadingStatus.Error -> {
                        if (DataHelper.arrBlackCentered.isNotEmpty()) {
                            dataReady = true
                            completeProgress {
                                if (minDelayPassed) navigateToNextScreen()
                            }
                        }
                    }
                    else -> {}
                }
            }
        }
    }

    private fun navigateToNextScreen() {
        if (!dataReady || DataHelper.arrBlackCentered.isEmpty()) return
        // Navigate theo logic của bạn
        finish()
    }

    override fun onBackPressed() {
        // Không cho phép back ở splash
    }

    override fun onDestroy() {
        super.onDestroy()
        progressAnimator?.cancel()
    }
}