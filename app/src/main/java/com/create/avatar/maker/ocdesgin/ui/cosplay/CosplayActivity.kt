package com.create.avatar.maker.ocdesgin.ui.cosplay

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Rect
import android.graphics.RectF
import android.net.ConnectivityManager
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.create.avatar.maker.ocdesgin.R
import com.create.avatar.maker.ocdesgin.base.AbsBaseActivity
import com.create.avatar.maker.ocdesgin.data.callapi.reponse.DataResponse
import com.create.avatar.maker.ocdesgin.data.callapi.reponse.LoadingStatus
import com.create.avatar.maker.ocdesgin.data.model.BodyPartModel
import com.create.avatar.maker.ocdesgin.data.model.ColorModel
import com.create.avatar.maker.ocdesgin.data.model.CustomModel
import com.create.avatar.maker.ocdesgin.data.repository.ApiRepository
import com.create.avatar.maker.ocdesgin.databinding.ActivityCosplayBinding
import com.create.avatar.maker.ocdesgin.databinding.ActivitySuccessBinding
import com.create.avatar.maker.ocdesgin.dialog.DialogExit
import com.create.avatar.maker.ocdesgin.ui.customview.CustomviewActivity
import com.create.avatar.maker.ocdesgin.ui.show.ShowActivity
import com.create.avatar.maker.ocdesgin.utils.CONST
import com.create.avatar.maker.ocdesgin.utils.DataHelper
import com.create.avatar.maker.ocdesgin.utils.changeText
import com.create.avatar.maker.ocdesgin.utils.hide
import com.create.avatar.maker.ocdesgin.utils.isInternetAvailable
import com.create.avatar.maker.ocdesgin.utils.newIntent
import com.create.avatar.maker.ocdesgin.utils.onSingleClick
import com.create.avatar.maker.ocdesgin.utils.show
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@AndroidEntryPoint
class CosplayActivity  : AbsBaseActivity<ActivityCosplayBinding>() {

    override fun getLayoutId(): Int = R.layout.activity_cosplay

    @Inject
    lateinit var apiRepository: ApiRepository
    private var randomModel: CustomModel? = null
    private var randomCoords: ArrayList<ArrayList<Int>>? = null  // cho ShowActivity
    private var bitmapCoords: ArrayList<ArrayList<Int>>? = null  // cho bitmap preview
    private var listImageSortView: ArrayList<String>? = null
    private var characterBitmap: Bitmap? = null
    private var loadingJob: Job? = null
    private var checkCallingDataOnline = false

    private val networkReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            val connectivityManager =
                context?.getSystemService(CONNECTIVITY_SERVICE) as ConnectivityManager
            val networkInfo = connectivityManager.activeNetworkInfo

            if (!checkCallingDataOnline) {
                if (networkInfo != null && networkInfo.isConnected) {
                    var hasOnlineData = false
                    DataHelper.arrBlackCentered.forEach {
                        if (it.checkDataOnline) {
                            hasOnlineData = true
                            return@forEach
                        }
                    }

                    if (!hasOnlineData) {
                        DataHelper.callApi(apiRepository)
                    }
                }
            }
        }
    }
    override fun onRestart() {
        super.onRestart()
    }

    override fun initView() {
        val space = " "
        val filter = IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION)
        registerReceiver(networkReceiver, filter)
        observeDataOnline()

        if (DataHelper.arrBg.isEmpty() || DataHelper.arrBlackCentered.isEmpty()) {
            finish()
            return
        }

        binding.apply {
            tv1.isSelected = true
            tv2.isSelected = true
            btnCosplay.isEnabled = false
            btnRandomize.isEnabled = false
            btnCosplay.alpha = 0.5f
            btnRandomize.alpha = 0.5f
            txtContentCosplay.text = TextUtils.concat(
                changeText(
                    this@CosplayActivity,
                    getString(R.string.tvCosplay1),
                    R.color.app_color,
                    R.font.hvd_comic_serif_pro
                ),
                space,
                changeText(
                    this@CosplayActivity,
                    getString(R.string.tvCosplay2),
                    R.color.white,
                    R.font.hvd_comic_serif_pro
                ),
                space,
                 changeText(
                    this@CosplayActivity,
                    getString(R.string.tvCosplay3),
                    R.color.app_color,
                    R.font.hvd_comic_serif_pro
                ),
                space,
                changeText(
                    this@CosplayActivity,
                    getString(R.string.tvCosplay4),
                    R.color.white,
                    R.font.hvd_comic_serif_pro
                ), space,
                changeText(
                    this@CosplayActivity,
                    getString(R.string.tvCosplay5),
                    R.color.app_color,
                    R.font.hvd_comic_serif_pro
                ),
                space,
                 changeText(
                    this@CosplayActivity,
                    getString(R.string.tvCosplay6),
                    R.color.white,
                    R.font.hvd_comic_serif_pro
                ),
                space,
                changeText(
                    this@CosplayActivity,
                    getString(R.string.tvCosplay7),
                    R.color.app_color,
                    R.font.hvd_comic_serif_pro
                ),space,
                changeText(
                    this@CosplayActivity,
                    getString(R.string.tvCosplay8),
                    R.color.white,
                    R.font.hvd_comic_serif_pro
                )

            )
        }
        randomizeCharacter()
    }


    private fun observeDataOnline() {
        DataHelper.arrDataOnline.observe(this) {
            it?.let {
                when (it.loadingStatus) {
                    LoadingStatus.Loading -> {
                        checkCallingDataOnline = true
                    }

                    LoadingStatus.Success -> {
                        if (DataHelper.arrBlackCentered.isNotEmpty() && !DataHelper.arrBlackCentered[0].checkDataOnline) {
                            checkCallingDataOnline = false
                            val listA = (it as DataResponse.DataSuccess).body ?: return@observe
                            checkCallingDataOnline = true
                            val sortedMap = listA
                                .toList()
                                .sortedBy { (_, list) ->
                                    list.firstOrNull()?.level ?: Int.MAX_VALUE
                                }
                                .toMap()
                            sortedMap.forEach { key, list ->
                                var a = arrayListOf<BodyPartModel>()
                                list.forEachIndexed { index, x10 ->
                                    var b = arrayListOf<ColorModel>()
                                    x10.colorArray.split(",").forEach { coler ->
                                        var c = arrayListOf<String>()
                                        if (coler == "") {
                                            for (i in 1..x10.quantity) {
                                                c.add(CONST.BASE_URL + "${CONST.BASE_CONNECT}/${x10.position}/${x10.parts}/${i}.png")
                                            }
                                            b.add(
                                                ColorModel(
                                                    "#",
                                                    c
                                                )
                                            )
                                        } else {
                                            for (i in 1..x10.quantity) {
                                                c.add(CONST.BASE_URL + "${CONST.BASE_CONNECT}/${x10.position}/${x10.parts}/${coler}/${i}.png")
                                            }
                                            b.add(
                                                ColorModel(
                                                    coler,
                                                    c
                                                )
                                            )
                                        }
                                    }
                                    a.add(
                                        BodyPartModel(
                                            "${CONST.BASE_URL}${CONST.BASE_CONNECT}$key/${x10.parts}/nav.png",
                                            b
                                        )
                                    )
                                }
                                var dataModel =
                                    CustomModel(
                                        "${CONST.BASE_URL}${CONST.BASE_CONNECT}$key/avatar.png",
                                        a,
                                        true
                                    )
                                dataModel.bodyPart.forEach { mbodyPath ->
                                    if (mbodyPath.icon.substringBeforeLast("/")
                                            .substringAfterLast("/").substringAfter("-") == "1"
                                    ) {
                                        mbodyPath.listPath.forEach {
                                            if (it.listPath[0] != "dice") {
                                                it.listPath.add(0, "dice")
                                            }
                                        }
                                    } else {
                                        mbodyPath.listPath.forEach {
                                            if (it.listPath[0] != "none") {
                                                it.listPath.add(0, "none")
                                                it.listPath.add(1, "dice")
                                            }
                                        }
                                    }
                                }
                                DataHelper.arrBlackCentered.add(0, dataModel)
                            }
                        }
                        checkCallingDataOnline = false
                    }

                    LoadingStatus.Error -> {
                        checkCallingDataOnline = false
                    }

                    else -> {
                        checkCallingDataOnline = true
                    }
                }
            }
        }
    }

    private fun randomizeCharacter() {
        loadingJob?.cancel()

        loadingJob = lifecycleScope.launch(Dispatchers.Default) {
            val connectivityManager = getSystemService(CONNECTIVITY_SERVICE) as ConnectivityManager
            val networkInfo = connectivityManager.activeNetworkInfo
            val hasInternet = networkInfo != null && networkInfo.isConnected

            val availableModels = if (hasInternet) {
                DataHelper.arrBlackCentered
            } else {
                DataHelper.arrBlackCentered.filter { !it.checkDataOnline }
            }

            if (availableModels.isEmpty()) {
                withContext(Dispatchers.Main) {
                    binding.progressBar.visibility = View.GONE
                }
                return@launch
            }

            randomModel = availableModels.random()

            randomModel?.let { model ->
                val list = ArrayList<String>().apply {
                    repeat(model.bodyPart.size) { add("") }
                }

                model.bodyPart.forEach {
                    val (x, _) = it.icon.substringBeforeLast("/")
                        .substringAfterLast("/")
                        .split("-")
                        .map { it.toInt() }
                    list[x - 1] = it.icon
                }
                listImageSortView = list

                // FIX: Logic random coords đã được sửa
                val coords = arrayListOf<ArrayList<Int>>()
                list.forEach { data ->
                    val bodyPart = model.bodyPart.find { it.icon == data }
                    val pair = if (bodyPart != null) {
                        val path = bodyPart.listPath[0].listPath
                        val color = bodyPart.listPath

                        // FIX: Bỏ qua "none" và "dice" khi random
                        val validIndices = path.indices.filter { idx ->
                            val value = path[idx]
                            value != "none" && value != "dice"
                        }

                        val randomValue = if (validIndices.isNotEmpty()) {
                            validIndices.random()
                        } else {
                            // Nếu không có giá trị hợp lệ, chọn index đầu tiên không phải none
                            if (path[0] == "none") 2 else 1
                        }

                        val randomColor = (0 until color.size).random()
                        arrayListOf(randomValue, randomColor)
                    } else {
                        arrayListOf(-1, -1)
                    }
                    coords.add(pair)
                }
                randomCoords = coords

                Log.d("RandomCat", "Generated coords: $coords")
                loadCharacterBitmap(model, list, coords)
            }
        }
    }



    private suspend fun loadCharacterBitmap(
        model: CustomModel,
        imageList: ArrayList<String>,
        coords: ArrayList<ArrayList<Int>>
    ) = withContext(Dispatchers.IO) {
        try {
            withContext(Dispatchers.Main) {
                binding.progressBar.visibility = View.GONE
                binding.imgCharacter.visibility = View.VISIBLE
                Glide.with(this@CosplayActivity)
                    .asGif()
                    .load(R.drawable.gif)
                    .into(binding.imgCharacter)
            }

            // Tính size
            val targetSize = calculateTargetSize(model, imageList, coords)
            val width = targetSize.first
            val height = targetSize.second

            // Tạo bitmap tổng
            val merged = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
            val canvas = Canvas(merged)
            val dstRect = RectF(0f, 0f, width.toFloat(), height.toFloat())

            var layerCount = 0

            // Load và vẽ TẤT CẢ layer trước khi hiển thị
            imageList.forEachIndexed { index, icon ->
                if (icon.isEmpty()) {
                    return@forEachIndexed
                }

                val coord = coords.getOrNull(index) ?: return@forEachIndexed

                if (coord[0] >= 0) {
                    val bodyPart = model.bodyPart.find { it.icon == icon }
                    val targetPath = bodyPart
                        ?.listPath?.getOrNull(coord[1])
                        ?.listPath?.getOrNull(coord[0])

                    if (!targetPath.isNullOrEmpty() && targetPath != "none" && targetPath != "dice") {
                        try {
                            val bmp = Glide.with(this@CosplayActivity)
                                .asBitmap()
                                .load(targetPath)
                                .override(width, height)
                                .diskCacheStrategy(DiskCacheStrategy.ALL)
                                .submit()
                                .get()

                            val srcRect = Rect(0, 0, bmp.width, bmp.height)
                            canvas.drawBitmap(bmp, srcRect, dstRect, null)
                            layerCount++

                            Log.d("RandomCat", "Drew layer $index successfully")
                        } catch (e: Exception) {
                            Log.e("RandomCat", "Error loading layer $index: ${e.message}")
                        }
                    }
                }
            }

            Log.d("RandomCat", "Total layers drawn: $layerCount")

            // FIX: Chỉ hiển thị nếu có ít nhất 1 layer được vẽ thành công
            if (layerCount > 0) {
                characterBitmap?.recycle()
                characterBitmap = merged

                withContext(Dispatchers.Main) {
                    // QUAN TRỌNG: Clear Glide trước để dừng gif
                    Glide.with(this@CosplayActivity).clear(binding.imgCharacter)
                    binding.imgCharacter.setImageBitmap(merged)
                    binding.apply {
                        btnCosplay.isEnabled = true
                        btnRandomize.isEnabled = true
                        btnCosplay.alpha = 1f
                        btnRandomize.alpha = 1f
                    }
                }
            } else {
                // Không có layer nào -> hiển thị placeholder hoặc thông báo lỗi
                merged.recycle()
                withContext(Dispatchers.Main) {
                    Glide.with(this@CosplayActivity).clear(binding.imgCharacter)
                    binding.imgCharacter.setImageResource(R.drawable.img_random_btn_randomone)
                    binding.apply {
                        btnCosplay.isEnabled = false
                        btnRandomize.isEnabled = true
                        btnCosplay.alpha = 0.5f
                        btnRandomize.alpha = 1f
                    }
                }
            }

        } catch (e: Exception) {
            e.printStackTrace()
            Log.e("RandomCat", "Error in loadCharacterBitmap: ${e.message}")
            withContext(Dispatchers.Main) {
                Glide.with(this@CosplayActivity).clear(binding.imgCharacter)
                binding.progressBar.visibility = View.GONE
                binding.imgCharacter.visibility = View.VISIBLE
                binding.imgCharacter.setImageResource(R.drawable.img_random_btn_randomone)
            }
        }
    }

    private suspend fun calculateTargetSize(
        model: CustomModel,
        listImageSortView: List<String>,
        coordSet: ArrayList<ArrayList<Int>>
    ): Pair<Int, Int> = withContext(Dispatchers.IO) {
        try {
            for (index in listImageSortView.indices) {
                val icon = listImageSortView[index]
                val coord = coordSet[index]

                // FIX: Thay đổi điều kiện để khớp với loadCharacterBitmap
                if (coord[0] > -1 && coord[1] > -1) {
                    val targetPath = model.bodyPart
                        .find { it.icon == icon }
                        ?.listPath?.getOrNull(coord[1])
                        ?.listPath?.getOrNull(coord[0])
                    if (!targetPath.isNullOrEmpty() && targetPath != "none" && targetPath != "dice") {
                        val bmp = Glide.with(this@CosplayActivity)
                            .asBitmap()
                            .load(targetPath)
                            .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
                            .submit()
                            .get()
                        val size = Pair(bmp.width / 2, bmp.height / 2)
                        bmp.recycle()
                        return@withContext size
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return@withContext Pair(512, 512)
    }


    override fun initAction() {
        binding.apply {
            imvBack.onSingleClick { finish() }

            btnRandomize.onSingleClick {
                btnCosplay.isEnabled = false
                btnRandomize.isEnabled = false
                btnCosplay.alpha = 0.5f
                btnRandomize.alpha = 0.5f

                imgCharacter.setImageBitmap(null)
                progressBar.visibility = View.VISIBLE
                imgCharacter.visibility = View.GONE
                Glide.with(this@CosplayActivity)
                    .asGif()
                    .load(R.drawable.gif)
                    .into(imgCharacter)

                characterBitmap?.recycle()
                characterBitmap = null

                randomizeCharacter()
            }
            btnCancel.onSingleClick {
                dialogCoplay.hide()
                dialogCoplay.setOnTouchListener(null)
            }
            imvGuide.onSingleClick {
                dialogCoplay.setOnTouchListener { _, _ -> true }
                dialogCoplay.show()
            }
            btnCosplay.onSingleClick {
                randomModel?.let { model ->
                    if (!isInternetAvailable(this@CosplayActivity) && model.checkDataOnline) {
                        DialogExit(this@CosplayActivity, "network").show()
                        return@onSingleClick
                    }
                    val index = DataHelper.arrBlackCentered.indexOf(model)
                    if (index != -1) {
                        lifecycleScope.launch(Dispatchers.IO) {
                            try {
                                // Lưu bitmap vào cache với chất lượng cao
                                // Xóa tất cả file preview cũ
                                cacheDir.listFiles { f -> f.name.startsWith("cosplay_preview_") }
                                    ?.forEach { it.delete() }

                                val cacheFile = java.io.File(cacheDir, "cosplay_preview_${System.currentTimeMillis()}.png")
                                characterBitmap?.let { bmp ->
                                    java.io.FileOutputStream(cacheFile).use { fos ->
                                        bmp.compress(Bitmap.CompressFormat.PNG, 100, fos)
                                        fos.flush()
                                    }
                                }
                                withContext(Dispatchers.Main) {
                                    startActivity(
                                        newIntent(this@CosplayActivity, ShowActivity::class.java)
                                            .putExtra("data", index)
                                            .putExtra("arr", randomCoords)
                                            .putExtra("imgCoslay", cacheFile.absolutePath)
                                    )
                                }
                            } catch (e: Exception) {
                                e.printStackTrace()
                                withContext(Dispatchers.Main) {
                                    startActivity(
                                        newIntent(this@CosplayActivity, ShowActivity::class.java)
                                            .putExtra("data", index)
                                            .putExtra("arr", randomCoords)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}