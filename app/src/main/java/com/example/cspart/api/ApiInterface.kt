package com.example.cspart.api

import com.example.cspart.models.*
import retrofit2.Call
import retrofit2.http.*

interface ApiInterface {

    @POST("home/login")
    @Headers(
        "Accept: application/json",
        "Content-type:application/json"
    )
    fun doLogin(@Body user: UserRequest) : Call<LoginResponse>

    /**
     * API lấy thông tin phiếu yêu cầu xuất kho theo requestCode
     * Created by tupt
     */
    @GET("ExportWarehouse/getexportwarehouse")
    fun getDeliveryRequestForm(@Query("requestCode") requestCode: String): Call<ExportWareHouseResponse>

    /**
     * API lấy thông tin chi tiết hàng hóa theo requestCode và materialCode
     * Created by tupt
     */
    @GET("ExportWarehouse/materialdetail")
    fun getDetailDeliveryRequestForm(@Query("requestCode") requestCode: String, @Query("materialCode") materialCode:String): Call<MaterialDetailResponse>

    /**
     * API update thông tin phiếu xuất kho detail
     * Created by tupt
     */
    @PUT("ExportWarehouse/updateexportwarehouse")
    fun updateExportWarehouse(@Body updateExportWareHouseData: ExportWareHouseDataRequest) : Call<UpdateExportWareHouseResponse>

    /**
     * API lấy thông tin phiếu nhập kho
     * Created by tupt
     */
    @GET("Input/getinput")
    fun getIntput(@Query("code") code: String): Call<Input>

    /**
     *API cập nhật nhập kho
     * Created by tupt
     */
    @PUT("Input/updateinput")
    fun updateInput(@Body data: PackListRequest): Call<InputUpdateResponse>

    /**
     * APi lấy thông tin phiếu xuất kho
     * Created by tupt
     */
    @GET("PackingList/getpackinglist")
    fun getPackList(@Query("code") code: String): Call<PackingListResponse>

    /**
     *API cập nhật phiếu xuất kho
     * Created by tupt
     */
    @PUT("PackingList/updatepackinglist")
    fun updatePackList(@Body data: PackListRequest): Call<InputUpdateResponse>

    /**
     * API lấy danh sách biên bản kiểm kho
     * Created by tupt
     */
    @GET("InventoryWarehouse/getinventorywarehouse")
    fun getInventoryWarehouse(@Query("code") code: String): Call<ReportResponse>

    /**
     * API cập nhật biên bản kiểm kho
     * Created by tupt
     */
    @PUT("InventoryWarehouse/updateinventorywarehouse")
    fun updateInventoryWarehouse(@Body data: PackListRequest): Call<InputUpdateResponse>

    @GET("PackingList/getstatusdelivery")
    fun getStatusDelivery(@Query("qrCode") code: String): Call<StatusDeliverResponse>

    @PUT("PackingList/updatestatusdelivery")
    fun updateStatusDelivery(@Query("qrCode") code: String): Call<StatusDeliverResponse>
}
