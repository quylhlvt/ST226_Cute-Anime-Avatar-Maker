package com.cute.anime.avatarmaker.ui.category

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.ConnectivityManager
import android.util.Log
import com.cute.anime.avatarmaker.base.AbsBaseActivity
import com.cute.anime.avatarmaker.data.callapi.reponse.DataResponse
import com.cute.anime.avatarmaker.data.callapi.reponse.LoadingStatus
import com.cute.anime.avatarmaker.data.model.BodyPartModel
import com.cute.anime.avatarmaker.data.model.ColorModel
import com.cute.anime.avatarmaker.data.model.CustomModel
import com.cute.anime.avatarmaker.data.repository.ApiRepository
import com.cute.anime.avatarmaker.dialog.DialogExit
import com.cute.anime.avatarmaker.ui.customview.CustomviewActivity
import com.cute.anime.avatarmaker.utils.CONST
import com.cute.anime.avatarmaker.utils.DataHelper
import com.cute.anime.avatarmaker.utils.isInternetAvailable
import com.cute.anime.avatarmaker.utils.newIntent
import com.cute.anime.avatarmaker.utils.onSingleClick
import com.cute.anime.avatarmaker.R
import com.cute.anime.avatarmaker.databinding.ActivityCategoryBinding
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class CategoryActivity : AbsBaseActivity<ActivityCategoryBinding>() {
    @Inject
    lateinit var apiRepository: ApiRepository
    val adapter by lazy { CategoryAdapter() }

    private var checkCallingDataOnline = false

    private val networkReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            val connectivityManager =
                context?.getSystemService(CONNECTIVITY_SERVICE) as ConnectivityManager
            val networkInfo = connectivityManager.activeNetworkInfo

            if (!checkCallingDataOnline) {
                if (networkInfo != null && networkInfo.isConnected) {
                    // Kiểm tra đã có data online chưa
                    var hasOnlineData = false
                    DataHelper.arrBlackCentered.forEach {
                        if (it.checkDataOnline) {
                            hasOnlineData = true
                            return@forEach
                        }
                    }

                    // Nếu chưa có data online thì gọi API
                    if (!hasOnlineData) {
                        DataHelper.callApi(apiRepository)
                    }
                }
            }
        }
    }

    override fun getLayoutId(): Int = R.layout.activity_category
    override fun onRestart() {
        super.onRestart()

    }

    override fun initView() {
        binding.imvBack.isSelected=true
        // Đăng ký broadcast receiver
        val filter = IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION)
        registerReceiver(networkReceiver, filter)

        // Observe data online
        observeDataOnline()

        if (DataHelper.arrBlackCentered.size <= 2 && !isInternetAvailable(this@CategoryActivity)) {
            DialogExit(
                this@CategoryActivity,
                "awaitdataHome"
            ).show()
        }

        if (DataHelper.arrBg.size == 0) {
            finish()
        } else {
            binding.rcv.itemAnimator = null
            binding.rcv.adapter = adapter
            adapter.submitList(DataHelper.arrBlackCentered)
        }
    }

    private fun observeDataOnline() {
        DataHelper.arrDataOnline.observe(this) {
            it?.let {
                when (it.loadingStatus) {
                    LoadingStatus.Loading -> {
                        checkCallingDataOnline = true
                    }

                    LoadingStatus.Success -> {
                        // Kiểm tra xem đã có data online chưa
                        var hasOnlineData = false
                        DataHelper.arrBlackCentered.forEach { model ->
                            if (model.checkDataOnline) {
                                hasOnlineData = true
                                return@forEach
                            }
                        }

                        if (!hasOnlineData) {
                            checkCallingDataOnline = false
                            val listA = (it as DataResponse.DataSuccess).body ?: return@observe
                            checkCallingDataOnline = true

                            val sortedMap = listA
                                .toList()
                                .sortedBy { (_, list) ->
                                    list.firstOrNull()?.level ?: Int.MAX_VALUE
                                }
                                .toMap()

                            val newOnlineDataList = arrayListOf<CustomModel>()

                            sortedMap.forEach { key, list ->
                                val bodyPartList = arrayListOf<BodyPartModel>()

                                list.forEachIndexed { index, x10 ->
                                    val colorList = arrayListOf<ColorModel>()

                                    x10.colorArray.split(",").forEach { color ->
                                        val pathList = arrayListOf<String>()

                                        if (color == "") {
                                            for (i in 1..x10.quantity) {
                                                pathList.add(CONST.BASE_URL + "${CONST.BASE_CONNECT}/${x10.position}/${x10.parts}/${i}.png")
                                            }
                                            colorList.add(ColorModel("#", pathList))
                                        } else {
                                            for (i in 1..x10.quantity) {
                                                pathList.add(CONST.BASE_URL + "${CONST.BASE_CONNECT}/${x10.position}/${x10.parts}/${color}/${i}.png")
                                            }
                                            colorList.add(ColorModel(color, pathList))
                                        }
                                    }

                                    bodyPartList.add(
                                        BodyPartModel(
                                            "${CONST.BASE_URL}${CONST.BASE_CONNECT}$key/${x10.parts}/nav.png",
                                            colorList
                                        )
                                    )
                                }

                                val dataModel = CustomModel(
                                    "${CONST.BASE_URL}${CONST.BASE_CONNECT}$key/avatar.png",
                                    bodyPartList,
                                    true
                                )

                                // Xử lý dice và none cho từng body part
                                dataModel.bodyPart.forEach { mbodyPath ->
                                    if (mbodyPath.icon.substringBeforeLast("/")
                                            .substringAfterLast("/").substringAfter("-") == "1"
                                    ) {
                                        mbodyPath.listPath.forEach { colorModel ->
                                            if (colorModel.listPath[0] != "dice") {
                                                colorModel.listPath.add(0, "dice")
                                            }
                                        }
                                    } else {
                                        mbodyPath.listPath.forEach { colorModel ->
                                            if (colorModel.listPath[0] != "none") {
                                                colorModel.listPath.add(0, "none")
                                                colorModel.listPath.add(1, "dice")
                                            }
                                        }
                                    }
                                }

                                newOnlineDataList.add(dataModel)
                            }

                            // Lấy data offline hiện tại (những data có checkDataOnline = false)
                            // Lấy data offline chưa bị duplicate
                            val currentOfflineData = DataHelper.arrBlackCentered
                                .filter { !it.checkDataOnline }
                                .distinctBy { it.avt } // Thêm distinct để tránh trùng

                            DataHelper.arrBlackCentered.clear()
                            newOnlineDataList.reversed().forEach { onlineData ->
                                DataHelper.arrBlackCentered.add(onlineData)
                            }
                            DataHelper.arrBlackCentered.addAll(currentOfflineData)

                            // Cập nhật adapter với danh sách mới
                            adapter.submitList(DataHelper.arrBlackCentered)
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

    override fun onDestroy() {
        super.onDestroy()
        try {
            unregisterReceiver(networkReceiver)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }



    override fun onBackPressed() {
        super.onBackPressed()
    }

    override fun initAction() {
        binding.apply {
            imvBack.onSingleClick {
                    finish()
            }
            adapter.onCLick = {
                if (DataHelper.arrBlackCentered[it].checkDataOnline) {
                    if (isInternetAvailable(this@CategoryActivity)) {
                        var a = DataHelper.arrBlackCentered[it].avt.split("/")
                        var b = a[a.size - 2]
                        Log.d("testKey","${b}")
                            startActivity(
                                newIntent(
                                    this@CategoryActivity,
                                    CustomviewActivity::class.java
                                ).putExtra("data", it)
                            )

                    } else {
                        DialogExit(
                            this@CategoryActivity,
                            "network"
                        ).show()
                    }
                } else {
                    var a = DataHelper.arrBlackCentered[it].avt.split("/")
                    var b = a[a.size - 2]
                    Log.d("testKey","${b}")
                    startActivity(
                        newIntent(
                            this@CategoryActivity,
                            CustomviewActivity::class.java
                        ).putExtra("data", it)
                    )

                }
            }
        }
    }
}