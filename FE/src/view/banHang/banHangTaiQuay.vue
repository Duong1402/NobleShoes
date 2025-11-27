<template>
  <div class="container-fluid mt-4 px-1">
    <div class="card shadow-sm border-0 mb-4">
      <div class="card-body py-2 px-3">
        <div
          class="page-header d-flex align-items-center justify-content-between"
        >
          <div>
            <h3 class="fw-bold text-warning mb-1">Bán hàng tại quầy</h3>
            <Breadcrumb class="mt-2 mb-0" />
          </div>
        </div>
      </div>
    </div>
    <div class="row g-3">
      <div class="col-md-8 d-flex flex-column gap-3">
        <!-- card 1 -->
        <div class="card p-3 text-center">
          <!-- Header -->
          <div
            class="d-flex justify-content-between align-items-center border-bottom pb-2 mb-3"
          >
            <h5 class="mb-0 card-title">
              <i class="fa-solid fa-list-ul me-1 text-warning"></i>Hóa đơn chờ
              <span v-if="hoaDon && hoaDon.ma"> - {{ hoaDon.ma }}</span>
            </h5>
            <button
              class="btn btn-warning text-white btn-sm"
              @click="handleTaoHoaDon"
            >
              <i class="fa-solid fa-plus me-1"></i>Tạo hóa đơn
            </button>
          </div>

          <!-- Nội dung -->
          <div class="border rounded p-3 bg-light-subtle">
            <div
              v-if="hoaDonChoList.length === 0"
              class="text-muted text-center"
            >
              <p class="mb-0">Chưa có hóa đơn chờ nào</p>
            </div>

            <div v-else class="hoa-don-container">
              <div
                v-for="hd in hoaDonChoList"
                :key="hd.id"
                class="card hoa-don-card text-start"
                :class="{
                  'border-warning border-2': hd.id === selectedHoaDonId,
                }"
                @click="selectHoaDon(hd.id)"
              >
                <div class="card-body p-2">
                  <!-- Hàng trên: mã + trạng thái -->
                  <div
                    class="d-flex justify-content-between align-items-center mb-1"
                  >
                    <h6 class="mb-0 fw-bold text-truncate">{{ hd.ma }}</h6>
                    <span
                      class="badge text-uppercase"
                      :class="{
                        'bg-success': hd.trangThai === 5,
                        'bg-danger': hd.trangThai === 0,
                        'bg-secondary':
                          hd.trangThai !== 0 && hd.trangThai !== 5,
                      }"
                    >
                      {{ trangThaiText(hd.trangThai) }}
                    </span>
                  </div>

                  <!-- Hàng dưới: tổng sản phẩm + nút xóa -->
                  <div
                    class="d-flex justify-content-between align-items-center"
                  >
                    <small class="text-muted">SP: {{ hd.soLuong || 0 }}</small>
                    <button
                      class="btn btn-sm btn-outline-danger py-0 px-1"
                      title="Hủy hóa đơn"
                      @click.stop="handleHuyHoaDon(hd.id)"
                    >
                      <i class="fa-solid fa-trash"></i>
                    </button>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </div>

        <!-- card 2 -->
        <div class="card p-3 text-center">
          <h5 class="mb-3 text-start">
            <i class="fas fa-shopping-cart me-2 text-warning"></i>
            Giỏ hàng
          </h5>
          <div
            class="border rounded p-2 bg-light-subtle"
            style="min-height: 120px; max-height: 400px; overflow-y: auto"
          >
            <template v-if="gioHang.length === 0">
              <div
                class="d-flex flex-column align-items-center justify-content-center py-4"
              >
                <div
                  class="bg-warning text-white rounded-circle d-flex align-items-center justify-content-center mb-3"
                  style="width: 50px; height: 50px; font-size: 1.5rem"
                >
                  <i class="fa-solid fa-cart-plus"></i>
                </div>

                <p class="text-muted text-center mb-0">Giỏ hàng trống</p>
              </div>
            </template>
            <template v-else>
              <ul class="list-group list-group-flush">
                <li
                  class="list-group-item d-flex align-items-start py-2 px-1 border-0 mb-2 rounded shadow-sm bg-white"
                  v-for="(sp, index) in gioHang"
                  :key="
                    sp.id + '-' + (sp.mauSac || '') + '-' + (sp.kichThuoc || '')
                  "
                >
                  <div
                    class="me-2 pt-2 fw-semibold text-muted"
                    style="width: 5%"
                  >
                    {{ index + 1 }}
                  </div>

                  <div class="me-3" style="width: 30%">
                    <img
                      :src="sp.hinhAnhUrl"
                      :alt="sp.tenSanPham"
                      class="cart-thumb"
                    />
                  </div>

                  <div
                    class="text-start flex-grow-1 me-2 pt-1"
                    style="width: 45%"
                  >
                    <p
                      class="h4 fw-bold mb-1 text-truncate"
                      style="max-width: 100%"
                    >
                      {{ sp.tenSanPham }}
                    </p>
                    <div class="text-muted mb-1">
                      <span class="badge bg-primary me-2">{{
                        sp.mauSac || "N/A"
                      }}</span>
                      <span class="badge bg-primary me-2">{{
                        sp.tenXuatXu || "N/A"
                      }}</span>
                      <span class="badge bg-primary">{{
                        sp.kichThuoc || "N/A"
                      }}</span>
                    </div>

                    <div class="d-flex align-items-center mt-2">
                      <p
                        class="mb-0 me-2 small fw-semibold"
                        style="white-space: nowrap"
                      >
                        Số lượng:
                      </p>

                      <div
                        class="input-group input-group-sm"
                        style="width: 125px"
                      >
                        <button
                          class="btn btn-outline-secondary py-0 px-1"
                          type="button"
                          @click.stop.prevent="
                            handleCapNhatSoLuong(sp.id, sp.soLuong - 1)
                          "
                          :disabled="sp.soLuong <= 1"
                        >
                          <i
                            class="fa-solid fa-minus"
                            style="font-size: 0.7rem"
                          ></i>
                        </button>

                        <input
                          type="number"
                          class="form-control text-center px-0 fw-bold"
                          :value="sp.soLuong"
                          min="1"
                          style="
                            width: 40px;
                            max-width: 45px;
                            font-size: 0.9rem;
                            height: 28px;
                          "
                          @blur.stop.prevent="
                            handleCapNhatSoLuong(sp.id, $event.target.value)
                          "
                          @keyup.enter.stop.prevent="
                            handleCapNhatSoLuong(sp.id, $event.target.value)
                          "
                        />

                        <button
                          class="btn btn-outline-secondary py-0 px-1"
                          type="button"
                          @click.stop.prevent="
                            handleCapNhatSoLuong(sp.id, sp.soLuong + 1)
                          "
                        >
                          <i
                            class="fa-solid fa-plus"
                            style="font-size: 0.7rem"
                          ></i>
                        </button>
                      </div>
                    </div>
                  </div>

                  <div
                    class="d-flex flex-column align-items-end justify-content-start pt-4"
                    style="width: 20%"
                  >
                    <small class="text-muted mb-1">
                      Đơn giá: {{ formatCurrency(sp.donGia) }}
                    </small>

                    <span class="h5 fw-bold text-warning mb-2">
                      {{ formatCurrency(sp.donGia * sp.soLuong) }}
                    </span>

                    <button
                      class="btn btn-sm btn-outline-danger py-0 px-1"
                      @click.stop.prevent="handleXoaSanPham(sp.id)"
                    >
                      <i class="fa-solid fa-trash"></i>
                    </button>
                  </div>
                </li>
              </ul>
            </template>
          </div>
        </div>

        <!-- card 3 -->
        <div class="card p-3 text-center">
          <div class="d-flex justify-content-between align-items-center mb-2">
            <h5 class="mb-0 text-start">
              <i class="fa-solid fa-table-list me-2 text-warning"></i>
              Danh sách sản phẩm
            </h5>
            <div></div>
          </div>

          <div
            class="d-flex align-items-center mb-3 flex-wrap"
            style="gap: 4px; justify-content: space-between"
          >
            <input
              v-model="searchSanPham"
              @input="filterSanPham"
              type="text"
              class="form-control rounded-pill border-warning shadow-sm"
              placeholder="Tìm kiếm sản phẩm..."
              style="flex-grow: 1; flex-basis: 120px"
            />

            <select
              v-model="filterMauSac"
              @change="filterSanPham"
              class="form-select rounded-3 border-warning"
              style="flex-grow: 1; flex-basis: 120px"
            >
              <option value="" selected>Tất cả Màu sắc</option>
              <option v-for="mau in listMauSac" :key="mau" :value="mau">
                {{ mau }}
              </option>
            </select>

            <select
              v-model="filterKichThuoc"
              @change="filterSanPham"
              class="form-select rounded-3 border-warning"
              style="flex-grow: 1; flex-basis: 120px"
            >
              <option value="" selected>Tất cả Kích thước</option>
              <option v-for="kt in listKichThuoc" :key="kt" :value="kt">
                {{ kt }}
              </option>
            </select>

            <select
              v-model="filterXuatXu"
              @change="filterSanPham"
              class="form-select rounded-3 border-warning"
              style="flex-grow: 1; flex-basis: 120px"
            >
              <option value="" selected>Tất cả Xuất xứ</option>
              <option v-for="xx in listXuatXu" :key="xx" :value="xx">
                {{ xx }}
              </option>
            </select>
          </div>

          <div
            class="table-responsive"
            style="max-height: 570px; overflow-y: auto"
          >
            <table class="table table-hover table-bordered align-middle">
              <thead class="table-warning text-center">
                <tr>
                  <th scope="col">#</th>
                  <th scope="col" style="width: 10%">Ảnh</th>
                  <th scope="col">Tên sản phẩm</th>
                  <th scope="col">Mã</th>
                  <th scope="col">Màu</th>
                  <th scope="col">Kích thước</th>
                  <th scope="col">Xuất xứ</th>
                  <th scope="col">Số lượng</th>
                  <th scope="col">Giá</th>
                  <th scope="col">Thao tác</th>
                </tr>
              </thead>
              <tbody>
                <tr v-if="filteredSanPham.length === 0">
                  <td colspan="10" class="text-center text-muted py-3">
                    <div
                      class="d-flex flex-column align-items-center justify-content-center"
                    >
                      <div
                        class="bg-warning text-white rounded-circle d-flex align-items-center justify-content-center mb-3"
                        style="width: 40px; height: 40px; font-size: 1.2rem"
                      >
                        <i class="fa-solid fa-exclamation"></i>
                      </div>
                      <span class="fw-semibold">
                        Không có sản phẩm nào được tìm thấy.
                      </span>
                    </div>
                  </td>
                </tr>
                <tr v-for="(sp, index) in paginatedSanPham" :key="sp.id">
                  <td class="text-center">
                    {{ (currentPage - 1) * itemsPerPage + index + 1 }}
                  </td>
                  <td class="text-center">
                    <img
                      :src="sp.hinhAnhUrl"
                      :alt="'Ảnh ' + sp.tenSanPham"
                      class="product-thumb"
                    />
                  </td>
                  <td class="text-start">
                    {{ sp.tenSanPham }}
                  </td>
                  <td class="text-start">
                    {{ sp.ma }}
                  </td>
                  <td class="text-start">
                    {{ sp.mauSac }}
                  </td>
                  <td class="text-start">
                    {{ sp.kichThuoc }}
                  </td>
                  <td class="text-start">
                    {{ sp.tenXuatXu }}
                  </td>
                  <td class="text-start">
                    {{ sp.soLuongTon }}
                  </td>
                  <td class="fw-bold text-warning">
                    {{ sp.giaBan?.toLocaleString() }} VND
                  </td>
                  <td class="text-center">
                    <button
                      class="btn btn-sm btn-warning text-white"
                      @click="handleThemSanPham(sp)"
                    >
                      <i class="fa-solid fa-cart-plus me-1"></i>
                    </button>
                  </td>
                </tr>
              </tbody>
            </table>
          </div>
          <div v-if="totalPages > 1" class="d-flex justify-content-end mt-3">
            <nav aria-label="Phân trang sản phẩm">
              <ul class="pagination pagination-sm mb-0">
                <li class="page-item" :class="{ disabled: currentPage === 1 }">
                  <a
                    class="page-link"
                    href="#"
                    @click.prevent="goToPage(currentPage - 1)"
                    >Trước</a
                  >
                </li>

                <li
                  class="page-item"
                  v-for="page in totalPages"
                  :key="page"
                  :class="{ active: page === currentPage }"
                >
                  <a
                    class="page-link"
                    href="#"
                    @click.prevent="goToPage(page)"
                    >{{ page }}</a
                  >
                </li>

                <li
                  class="page-item"
                  :class="{ disabled: currentPage === totalPages }"
                >
                  <a
                    class="page-link"
                    href="#"
                    @click.prevent="goToPage(currentPage + 1)"
                    >Sau</a
                  >
                </li>
              </ul>
            </nav>
          </div>
        </div>
      </div>

      <div class="col-md-4 d-flex flex-column gap-3">
        <!-- Nếu chưa chọn hóa đơn -->
        <div v-if="!hoaDon" class="card p-3 text-center">
          <h5 class="text-muted mb-0">Chưa có hóa đơn được chọn</h5>
        </div>

        <!-- Nếu đã có hóa đơn -->
        <template v-else>
          <!-- card 4 -->
          <div v-if="hoaDon && hoaDon.khachHang" class="card p-3 text-center">
            <h5 class="mb-3 text-start">
              <i class="fas fa-user me-2 text-warning"></i>Khách hàng
            </h5>

            <div class="position-relative">
              <div class="input-group mb-4">
                <input
                  type="text"
                  class="form-control"
                  v-model="searchKeyword"
                  placeholder="Tìm tên khách hàng, sđt"
                  @keyup.enter="handleTimKhachHang"
                  @blur="handleBlurSearch"
                />
              </div>

              <div
                v-if="searchResults.length > 0"
                class="search-results-dropdown"
              >
                <ul class="list-group list-group-flush shadow">
                  <li
                    v-for="kh in searchResults"
                    :key="kh.id"
                    class="list-group-item list-group-item-action"
                    @mousedown.prevent="handleSelectKhachHang(kh)"
                  >
                    <div class="d-flex flex-column text-start">
                      <div class="fw-bold text-dark">
                        {{ kh.hoTen }}
                      </div>
                      <div class="small text-muted mt-0">
                        {{ kh.sdt }} | Mã: {{ kh.ma }}
                      </div>
                    </div>
                  </li>
                </ul>
              </div>
            </div>
            <div class="text-start">
              <div class="row g-2 mb-1">
                <div class="col-6">
                  <label
                    for="hoTen"
                    class="form-label fw-bold small mb-0 text-dark"
                  >
                    Tên khách hàng <span class="text-danger">*</span>
                  </label>
                </div>
                <div class="col-6">
                  <label
                    for="sdt"
                    class="form-label fw-bold small mb-0 text-dark"
                  >
                    Số điện thoại <span class="text-danger">*</span>
                  </label>
                </div>
              </div>

              <div class="row g-2 mb-3">
                <div class="col-6">
                  <div class="input-group">
                    <span class="input-group-text">
                      <i class="fa-regular fa-user"></i>
                    </span>
                    <input
                      id="hoTen"
                      type="text"
                      class="form-control"
                      v-model="hoaDon.khachHang.hoTen"
                      placeholder="Tên khách hàng"
                      disabled
                      @blur="handleCapNhatKhachHang(hoaDon.khachHang)"
                    />
                  </div>
                </div>
                <div class="col-6">
                  <div class="input-group">
                    <span class="input-group-text">
                      <i class="fa-solid fa-phone"></i>
                    </span>
                    <input
                      id="sdt"
                      type="text"
                      class="form-control"
                      v-model="hoaDon.khachHang.sdt"
                      placeholder="Số điện thoại"
                      disabled
                      @blur="handleCapNhatKhachHang(hoaDon.khachHang)"
                    />
                  </div>
                </div>
              </div>
            </div>

            <div class="d-grid gap-2 mt-2">
              <button
                class="btn btn-outline-warning btn-sm"
                @click="handleThemNhanhKhachHang"
              >
                <i class="fa-solid fa-user-plus me-1"></i>Thêm Khách hàng mới
              </button>
            </div>
          </div>

          <!-- card 5 -->
          <div class="card p-3 text-center flex-grow-1 big-card">
            <div class="d-flex justify-content-between align-items-center mb-3">
              <h5 class="mb-0 text-start">
                <i class="fa-solid fa-receipt me-2 text-warning"></i>Thông tin
                đơn
              </h5>
              <div class="form-check form-switch">
                <input
                  class="form-check-input"
                  type="checkbox"
                  id="banGiaoHangToggle"
                  v-model="isBanGiaoHang"
                />
                <label class="form-check-label fw-bold" for="banGiaoHangToggle"
                  >Bán giao hàng</label
                >
              </div>
            </div>

            <div
              v-if="isBanGiaoHang"
              class="card p-3 mb-3 text-start border-warning"
            >
              <div
                class="d-flex justify-content-between align-items-center mb-3"
              >
                <h5 class="fw-bold mb-0 text-dark">Thông tin người nhận</h5>
                <button
                  class="btn btn-sm btn-outline-warning"
                  @click="handleOpenModalDiaChi"
                >
                  <i class="fa-solid fa-map-location-dot me-1"></i> Chọn địa chỉ
                </button>
              </div>

              <div class="row g-3">
                <div class="col-md-6">
                  <label for="tenNguoiNhan" class="form-label text-start w-100"
                    >Tên người nhận</label
                  >
                  <input
                    type="text"
                    class="form-control"
                    id="tenNguoiNhan"
                    v-model="thongTinNguoiNhan.hoTen"
                    placeholder="Nhập tên..."
                    disabled
                  />
                </div>

                <div class="col-md-6">
                  <label for="sdt" class="form-label text-start w-100"
                    >Số điện thoại</label
                  >
                  <input
                    type="tel"
                    class="form-control"
                    id="sdt"
                    v-model="thongTinNguoiNhan.sdt"
                    placeholder="Nhập SĐT..."
                    disabled
                  />
                </div>

                <div class="col-md-6">
                  <label for="tinhThanh" class="form-label w-100 small fw-bold"
                    >Tỉnh/Thành phố</label
                  >
                  <select
                    class="form-select"
                    v-model="provinceCode"
                    @change="handleProvinceChange"
                  >
                    <option value="" disabled>Chọn Tỉnh/Thành</option>
                    <option
                      v-for="p in provinces"
                      :key="p.code"
                      :value="p.code"
                    >
                      {{ p.name }}
                    </option>
                  </select>
                </div>

                <div class="col-md-6">
                  <label for="quanHuyen" class="form-label w-100 small fw-bold"
                    >Quận/Huyện</label
                  >
                  <select
                    class="form-select"
                    v-model="districtCode"
                    @change="handleDistrictChange"
                    :disabled="!provinceCode"
                  >
                    <option value="" disabled>Chọn Quận/Huyện</option>
                    <option
                      v-for="d in districts"
                      :key="d.code"
                      :value="d.code"
                    >
                      {{ d.name }}
                    </option>
                  </select>
                </div>

                <div class="col-md-6">
                  <label for="phuongXa" class="form-label w-100 small fw-bold"
                    >Phường/Xã</label
                  >
                  <select
                    class="form-select"
                    v-model="wardCode"
                    @change="handleWardChange"
                    :disabled="!districtCode"
                  >
                    <option value="" disabled>Chọn Phường/Xã</option>
                    <option v-for="w in wards" :key="w.code" :value="w.code">
                      {{ w.name }}
                    </option>
                  </select>
                </div>

                <div class="col-12">
                  <label
                    for="diaChiCuThe"
                    class="form-label w-100 small fw-bold"
                    >Địa chỉ cụ thể</label
                  >
                  <input
                    type="text"
                    class="form-control"
                    id="diaChiCuThe"
                    v-model="thongTinNguoiNhan.diaChiCuThe"
                    placeholder="Số nhà, ngõ, tên đường..."
                  />
                </div>
              </div>
            </div>

            <div class="border rounded p-2 bg-light-subtle mb-3">
              <div class="d-flex justify-content-between mb-1">
                <span class="text-muted">Tổng tiền hàng:</span>
                <span class="text-muted fw-bold">
                  {{ (tongTienHang ?? 0).toLocaleString() }} VND
                </span>
              </div>

              <div class="d-flex justify-content-between mb-2">
                <span class="text-danger">Giảm giá:</span>
                <span class="text-danger fw-bold">
                  - {{ (soTienGiamGia ?? 0).toLocaleString() }} VND
                </span>
              </div>

              <hr class="my-1" />

              <div class="d-flex justify-content-between pt-1">
                <span class="fw-bold">THÀNH TIỀN:</span>
                <span class="fw-bolder fs-5 text-warning">
                  {{ (tongTienSauGiam ?? 0).toLocaleString() }} VND
                </span>
              </div>
            </div>

            <div class="mb-3 text-start">
              <h6 class="fw-bold">Phương thức thanh toán</h6>

              <div class="d-flex justify-content-between gap-2">
                <div class="flex-fill">
                  <button
                    @click="handleChonPhuongThuc('CHUYEN_KHOAN')"
                    :class="{
                      'btn-warning text-white':
                        phuongThucThanhToan === 'CHUYEN_KHOAN',
                      'btn-outline-secondary':
                        phuongThucThanhToan !== 'CHUYEN_KHOAN',
                    }"
                    class="btn w-100"
                  >
                    Chuyển khoản
                  </button>
                </div>

                <div class="flex-fill">
                  <button
                    @click="handleChonPhuongThuc('TIEN_MAT')"
                    :class="{
                      'btn-warning text-white':
                        phuongThucThanhToan === 'TIEN_MAT',
                      'btn-outline-secondary':
                        phuongThucThanhToan !== 'TIEN_MAT',
                    }"
                    class="btn w-100"
                  >
                    Tiền mặt
                  </button>
                </div>

                <div class="flex-fill">
                  <button
                    @click="handleChonPhuongThuc('CA_HAI')"
                    :class="{
                      'btn-warning text-white':
                        phuongThucThanhToan === 'CA_HAI',
                      'btn-outline-secondary': phuongThucThanhToan !== 'CA_HAI',
                    }"
                    class="btn w-100"
                  >
                    Cả hai
                  </button>
                </div>
              </div>
            </div>

            <button
              class="btn btn-warning w-100 fw-bold"
              @click="handleThanhToan"
              :disabled="!hoaDon || isVnpayProcessing"
            >
              <span v-if="isVnpayProcessing">Đang tạo giao dịch VNPay...</span>
              <span v-else>Thanh toán</span>
            </button>
          </div>
        </template>
      </div>
    </div>
  </div>
  <div
    v-if="showDiaChiModal"
    class="modal d-block"
    tabindex="-1"
    style="background-color: rgba(0, 0, 0, 0.5)"
  >
    <div class="modal-dialog modal-dialog-centered modal-lg">
      <div class="modal-content">
        <div class="modal-header bg-warning text-white">
          <h5 class="modal-title fw-bold">Danh sách địa chỉ khách hàng</h5>
          <button
            type="button"
            class="btn-close"
            @click="showDiaChiModal = false"
          ></button>
        </div>

        <div class="modal-body" style="max-height: 400px; overflow-y: auto">
          <div v-if="hoaDon?.khachHang?.danhSachDiaChi?.length > 0">
            <div class="list-group">
              <button
                v-for="dc in hoaDon.khachHang.danhSachDiaChi"
                :key="dc.id"
                class="list-group-item list-group-item-action d-flex justify-content-between align-items-center"
                @click="handleChonDiaChiTuModal(dc)"
              >
                <div class="text-start">
                  <div class="fw-bold text-dark">
                    <span v-if="dc.macDinh" class="badge bg-danger me-2"
                      >Mặc định</span
                    >
                    {{ dc.diaChiCuThe }}
                  </div>
                  <small class="text-muted">
                    {{ dc.xa }} - {{ dc.huyen }} - {{ dc.thanhPho }}
                  </small>
                </div>
                <i class="fa-solid fa-chevron-right text-warning"></i>
              </button>
            </div>
          </div>

          <div v-else class="text-center py-4 text-muted">
            <i class="fa-solid fa-box-open fs-1 mb-2"></i>
            <p>Khách hàng này chưa có địa chỉ nào được lưu.</p>
          </div>
        </div>

        <div class="modal-footer">
          <button
            type="button"
            class="btn btn-secondary"
            @click="showDiaChiModal = false"
          >
            Đóng
          </button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, watch, onMounted } from "vue";
import Breadcrumb from "@/components/common/Breadcrumb.vue";
import {
  taoHoaDon,
  huyHoaDon as apiHuyHoaDon,
  themSanPhamVaoHoaDon,
  xoaSanPhamKhoiHoaDon,
  capNhatKhachHang,
  apDungGiamGia,
  thanhToan,
  getChiTietHoaDon,
  getDanhSachSanPham,
  timKhachHangBySdt,
  themKhachHangMoi,
  timKhachHangDaDangKy,
} from "@/service/BanHangService";
import Swal from "sweetalert2";
import { useNotify } from "@/composables/useNotify";
import router from "@/router";
import axios from "axios";

const notify = useNotify();
const idNhanVien = "02b6c170-6aa5-4cc7-8e52-abc123456789";

// state
const hoaDonChoList = ref([]);
const selectedHoaDonId = ref(null);
const hoaDon = ref(null); // hóa đơn đang chọn
const gioHang = ref([]);
const savedList = localStorage.getItem("hoaDonChoList");
const savedSelectedId = localStorage.getItem("selectedHoaDonId");

const filterMauSac = ref("");
const filterKichThuoc = ref("");
const filterXuatXu = ref("");
const searchSanPham = ref("");
const filteredSanPham = ref([]);
const searchKeyword = ref("");
const searchResults = ref([]);

const isGuestEditable = ref(false);
const showAddGuestButton = ref(false);

const danhSachSanPham = ref([]);

const currentPage = ref(1);
const itemsPerPage = 5; // Số phần tử mỗi trang

const showDiaChiModal = ref(false);
const isBanGiaoHang = ref(false); // Trạng thái của Toggle "Bán giao hàng"
const thongTinNguoiNhan = ref({
  hoTen: "",
  sdt: "",
  tinhThanh: "",
  quanHuyen: "",
  phuongXa: "",
  diaChiCuThe: "",
});

const totalPages = computed(() => {
  return Math.ceil(filteredSanPham.value.length / itemsPerPage);
});

// Danh sách sản phẩm chỉ hiển thị trên trang hiện tại
const paginatedSanPham = computed(() => {
  const start = (currentPage.value - 1) * itemsPerPage;
  const end = start + itemsPerPage;
  return filteredSanPham.value.slice(start, end);
});

// Hàm chuyển trang
const goToPage = (page) => {
  if (page >= 1 && page <= totalPages.value) {
    currentPage.value = page;
  }
};

// Đảm bảo về trang 1 khi danh sách sản phẩm thay đổi (do lọc/tìm kiếm)
watch(filteredSanPham, () => {
  currentPage.value = 1;
});

// Hàm định dạng tiền tệ đơn giản trong
const formatCurrency = (amount) => {
  if (amount === null || amount === undefined) return "0 ₫";
  return amount.toLocaleString("vi-VN", { style: "currency", currency: "VND" });
};

// trạng thái
const trangThaiText = (value) => {
  const map = {
    0: "Đã hủy",
    1: "Chờ xác nhận",
    2: "Đã xác nhận",
    4: "Đang giao",
    5: "Hoàn thành",
    3: "Chờ thanh toán",
  };
  return map[value] || "Không xác định";
};

const listMauSac = computed(() => {
  const maus = danhSachSanPham.value.map((sp) => sp.mauSac).filter(Boolean);
  return [...new Set(maus)];
});

const listKichThuoc = computed(() => {
  const kts = danhSachSanPham.value.map((sp) => sp.kichThuoc).filter(Boolean);
  return [...new Set(kts)];
});

const listXuatXu = computed(() => {
  const xxs = danhSachSanPham.value.map((sp) => sp.tenXuatXu).filter(Boolean);
  return [...new Set(xxs)];
});

const filterSanPham = () => {
  if (!Array.isArray(danhSachSanPham.value)) {
    filteredSanPham.value = [];
    return;
  }

  const keyword = searchSanPham.value.trim().toLowerCase();

  filteredSanPham.value = danhSachSanPham.value.filter((sp) => {
    const ten = sp.tenSanPham?.toLowerCase() || sp.ten?.toLowerCase() || "";
    const ma = sp.ma?.toLowerCase() || "";
    const mau = sp.mauSac?.toLowerCase() || "";
    const kt = sp.kichThuoc?.toLowerCase() || "";
    const xx = sp.tenXuatXu?.toLowerCase() || "";

    const matchesSearch =
      ten.includes(keyword) ||
      ma.includes(keyword) ||
      mau.includes(keyword) ||
      kt.includes(keyword) ||
      xx.includes(keyword);

    const matchesMauSac =
      !filterMauSac.value || mau === filterMauSac.value.toLowerCase();

    const matchesKichThuoc =
      !filterKichThuoc.value || kt === filterKichThuoc.value.toLowerCase();

    const matchesXuatXu =
      !filterXuatXu.value || xx === filterXuatXu.value.toLowerCase();

    return (
      sp.soLuongTon > 0 &&
      matchesSearch &&
      matchesMauSac &&
      matchesKichThuoc &&
      matchesXuatXu
    );
  });
};

// tạo hóa đơn
async function handleTaoHoaDon() {
  if (hoaDonChoList.value.length >= 5) {
    Swal.fire("Giới hạn 5 hóa đơn chờ!", "", "warning");
    return;
  }
  try {
    const res = await taoHoaDon(idNhanVien);
    const newHoaDon = {
      ...res.data,
      tongSoLuong: 0,
      sanPhamList: [],
      khachHang: khachLeMacDinh,
    };
    hoaDonChoList.value.push(newHoaDon);
    selectedHoaDonId.value = newHoaDon.id;
    hoaDon.value = newHoaDon;
    gioHang.value = [];
    tongTienHang.value = 0;

    thongTinNguoiNhan.value = {
      hoTen: "",
      sdt: "",
      tinhThanh: "",
      quanHuyen: "",
      phuongXa: "",
      diaChiCuThe: "",
    };
    isBanGiaoHang.value = false;

    notify.success("Tạo hóa đơn mới thành công!");
  } catch (err) {
    console.error(err);
    notify.error("Tạo hóa đơn thất bại!");
  }
}

// chọn hóa đơn
// Tìm đến hàm selectHoaDon và sửa lại:
const selectHoaDon = (id) => {
  selectedHoaDonId.value = id;
  hoaDon.value = hoaDonChoList.value.find((h) => h.id === id) || null;
  gioHang.value = hoaDon.value?.sanPhamList || [];
  tongTienHang.value = gioHang.value.reduce((sum, sp) => sum + sp.thanhTien, 0);

  const currentKhachHang = hoaDon.value?.khachHang;

  // Logic hiển thị nút sửa khách lẻ
  isGuestEditable.value =
    !currentKhachHang ||
    currentKhachHang.id === khachLeMacDinh.id ||
    currentKhachHang.ma === khachLeMacDinh.ma;

  searchKeyword.value = hoaDon.value?.khachHang?.sdt || "";
  showAddGuestButton.value = false;

  // === 🔥 CODE THÊM MỚI: RESET FORM GIAO HÀNG THEO HÓA ĐƠN ===
  if (currentKhachHang && currentKhachHang.id !== KHACH_LE_ID) {
    // Nếu là khách quen, điền thông tin vào form giao hàng
    thongTinNguoiNhan.value = {
      ...thongTinNguoiNhan.value, // Giữ lại các trường địa chỉ nếu muốn
      hoTen: currentKhachHang.hoTen,
      sdt: currentKhachHang.sdt,
    };
  } else {
    // Nếu là khách lẻ hoặc hóa đơn mới, reset trắng hoặc để trống
    thongTinNguoiNhan.value.hoTen = "";
    thongTinNguoiNhan.value.sdt = "";
  }
};

const KHACH_LE_ID = 1;

// 1. Định nghĩa Khách vãng lai Mặc định
const khachLeMacDinh = {
  id: KHACH_LE_ID,
  hoTen: "Khách lẻ",
  sdt: "0000000000",
  // Thêm các trường khác nếu cần (ví dụ: email: '', diaChi: '')
};

// Hàm XỬ LÝ KHI MẤT FOCUS KHỎI INPUT TÌM KIẾM
const handleBlurSearch = () => {
  setTimeout(() => {
    searchResults.value = [];
  }, 0);
};

// Hàm TÌM KIẾM KHÁCH HÀNG
const handleTimKhachHang = async () => {
  const keywword = searchKeyword.value.trim();
  if (keywword.length < 2) {
    searchResults.value = [];
    if (!keywword) {
      return notify.warning("Vui lòng nhập Tên/SĐT để tìm kiếm!");
    }
    return;
  }

  try {
    const res = await timKhachHangDaDangKy(keywword); // Gọi API tìm kiếm

    if (res.data && res.data.length > 0) {
      searchResults.value = res.data;
      if (res.data.length === 1) {
        const foundKhachHang = res.data[0];
        await assignKhachHang(foundKhachHang);
        notify.success("Đã tìm thấy 1 Khách hàng!");
      } else {
        notify.info(`Tìm thấy ${res.data.length} Khách hàng.`);
      }
      showAddGuestButton.value = false;
    } else {
      // ⚠️ KHÔNG TÌM THẤY
      searchResults.value = [];
      showAddGuestButton.value = true;
      notify.warning("Không tìm thấy Khách hàng. Bạn có thể thêm mới nhanh.");
    }
  } catch (err) {
    console.error("Lỗi tìm kiếm KH:", err);
    notify.error("Lỗi khi tìm kiếm Khách hàng!");
  }
};

// --- 1. Khai báo State cho Địa chỉ ---
const provinces = ref([]); // Danh sách toàn bộ Tỉnh/Thành
const provinceCode = ref("");
const districtCode = ref("");
const wardCode = ref("");

// --- 2. Computed để lọc Huyện/Xã theo cấp cha ---
const districts = computed(() => {
  if (!provinceCode.value) return [];
  const foundProvince = provinces.value.find(
    (p) => p.code == provinceCode.value
  );
  return foundProvince ? foundProvince.districts : [];
});

const wards = computed(() => {
  if (!districtCode.value) return [];
  const foundDistrict = districts.value.find(
    (d) => d.code == districtCode.value
  );
  return foundDistrict ? foundDistrict.wards : [];
});

// --- 3. Load dữ liệu API (Chạy khi mount) ---
onMounted(async () => {
  try {
    // Gọi API độ sâu 3 để lấy full cây Tỉnh -> Huyện -> Xã
    const res = await axios.get("https://provinces.open-api.vn/api/?depth=3");
    provinces.value = res.data;
  } catch (error) {
    console.error("Lỗi load tỉnh thành:", error);
  }
});

// --- 4. Hàm xử lý khi chọn Dropdown ---

// Khi chọn Tỉnh: Gán tên vào thongTinNguoiNhan, reset Huyện/Xã
const handleProvinceChange = () => {
  const prov = provinces.value.find((p) => p.code == provinceCode.value);
  if (prov) {
    thongTinNguoiNhan.value.tinhThanh = prov.name;
  } else {
    thongTinNguoiNhan.value.tinhThanh = "";
  }
  // Reset cấp dưới
  districtCode.value = "";
  wardCode.value = "";
  thongTinNguoiNhan.value.quanHuyen = "";
  thongTinNguoiNhan.value.phuongXa = "";
};

// Khi chọn Huyện
const handleDistrictChange = () => {
  const dist = districts.value.find((d) => d.code == districtCode.value);
  if (dist) {
    thongTinNguoiNhan.value.quanHuyen = dist.name;
  } else {
    thongTinNguoiNhan.value.quanHuyen = "";
  }
  // Reset cấp dưới
  wardCode.value = "";
  thongTinNguoiNhan.value.phuongXa = "";
};

// Khi chọn Xã
const handleWardChange = () => {
  const wd = wards.value.find((w) => w.code == wardCode.value);
  if (wd) {
    thongTinNguoiNhan.value.phuongXa = wd.name;
  } else {
    thongTinNguoiNhan.value.phuongXa = "";
  }
};

// --- 5. Hàm tiện ích: Tìm Mã từ Tên (Dùng để map dữ liệu khách hàng vào Select) ---
// Hàm này quan trọng để khi bạn chọn khách hàng, Select box tự nhảy đúng giá trị
const syncAddressToSelects = () => {
  // 1. Tìm Tỉnh
  const foundProv = provinces.value.find(
    (p) => p.name === thongTinNguoiNhan.value.tinhThanh
  );
  if (foundProv) {
    provinceCode.value = foundProv.code;

    // 2. Tìm Huyện (trong tỉnh đó)
    const foundDist = foundProv.districts.find(
      (d) => d.name === thongTinNguoiNhan.value.quanHuyen
    );
    if (foundDist) {
      districtCode.value = foundDist.code;

      // 3. Tìm Xã (trong huyện đó)
      const foundWard = foundDist.wards.find(
        (w) => w.name === thongTinNguoiNhan.value.phuongXa
      );
      if (foundWard) {
        wardCode.value = foundWard.code;
      } else {
        wardCode.value = "";
      }
    } else {
      districtCode.value = "";
      wardCode.value = "";
    }
  } else {
    provinceCode.value = "";
    districtCode.value = "";
    wardCode.value = "";
  }
};

const assignKhachHang = async (khachHang) => {
  try {
    console.log("Dữ liệu khách hàng nhận được:", khachHang);

    // Gọi API cập nhật khách cho hóa đơn
    await capNhatKhachHang(hoaDon.value.id, khachHang.id);

    // Cập nhật UI phần khách hàng
    hoaDon.value.khachHang = khachHang;
    isGuestEditable.value = false;
    searchResults.value = [];
    searchKeyword.value = khachHang.hoTen;

    // === 🔥 BẮT ĐẦU ĐIỀN FORM GIAO HÀNG ===

    // 1. Điền Tên và SĐT
    thongTinNguoiNhan.value.hoTen = khachHang.hoTen || "";
    thongTinNguoiNhan.value.sdt = khachHang.sdt || "";

    // 2. Tìm danh sách địa chỉ
    // 🔴 SỬA LẠI DÒNG NÀY: Thêm 'danhSachDiaChi' vào đầu tiên
    const listDiaChi = khachHang.danhSachDiaChi || khachHang.listDiaChi || [];

    console.log("Danh sách địa chỉ tìm thấy:", listDiaChi);

    if (listDiaChi.length > 0) {
      // 🟢 KHAI BÁO BIẾN diaChiChon Ở ĐÂY (Bên trong IF)
      const diaChiChon = listDiaChi.find((d) => d.macDinh) || listDiaChi[0];

      console.log("Địa chỉ được chọn:", diaChiChon);

      // C. Điền thông tin địa chỉ vào biến Text (Data Binding)
      // Lưu ý: Kiểm tra kỹ tên trường API trả về (thanhPho hay tinhThanh, huyen hay quanHuyen...)
      thongTinNguoiNhan.value.tinhThanh =
        diaChiChon.thanhPho || diaChiChon.tinhThanh || "";
      thongTinNguoiNhan.value.quanHuyen =
        diaChiChon.huyen || diaChiChon.quanHuyen || "";
      thongTinNguoiNhan.value.phuongXa =
        diaChiChon.xa || diaChiChon.phuongXa || "";
      thongTinNguoiNhan.value.diaChiCuThe = diaChiChon.diaChiCuThe || "";

      // D. 🔥 QUAN TRỌNG: Đồng bộ từ Text sang Select Box
      // Cần setTimeout để chờ Vue cập nhật giá trị text xong thì mới map sang Code
      setTimeout(() => {
        syncAddressToSelects();
      }, 100);
    } else {
      // Nếu không có địa chỉ nào, reset form
      thongTinNguoiNhan.value.tinhThanh = "";
      thongTinNguoiNhan.value.quanHuyen = "";
      thongTinNguoiNhan.value.phuongXa = "";
      thongTinNguoiNhan.value.diaChiCuThe = "";

      provinceCode.value = "";
      districtCode.value = "";
      wardCode.value = "";
    }

    notify.success("Đã cập nhật thông tin khách hàng!");
  } catch (error) {
    console.error("Lỗi assignKhachHang:", error);
    notify.error("Lỗi khi chọn khách hàng!");
  }
};

// Bạn sẽ gọi hàm này từ giao diện Modal/Dropdown
const handleSelectKhachHang = (khachHang) => {
  assignKhachHang(khachHang);
  notify.success(`Đã chọn Khách hàng: ${khachHang.hoTen}`);
};

// Hàm THÊM NHANH KHÁCH HÀNG MỚI
const handleThemNhanhKhachHang = async () => {
  await Swal.fire({
    title: `<span style="font-weight: bold; font-size: 1.3rem;">Thêm khách hàng mới</span>`,
    html: `

      <style>
        #swalHoTen, #swalSdt {
          text-align: left !important;
          border: 1px solid #ffc107 !important;
        }
        #swalHoTen:focus, #swalSdt:focus {
          border-color: #ffca2c !important;
          box-shadow: 0 0 3px #ffe082 !important;
        }
        .error-text {
          color: red;
          font-size: 0.8rem;
          margin-top: 4px;
          min-height: 16px;
        }
      </style>

      <div style="display: flex; gap: 16px; width: 100%;">
        <div style="flex: 1; display: flex; flex-direction: column; text-align: left;">
          <label for="swalHoTen" style="font-weight: bold; margin-bottom: 6px;">Tên khách hàng *</label>
          <input id="swalHoTen" class="swal2-input" placeholder="Tên khách hàng" style="margin:0;">
          <small id="errHoTen" class="error-text"></small>
        </div>

        <div style="flex: 1; display: flex; flex-direction: column; text-align: left;">
          <label for="swalSdt" style="font-weight: bold; margin-bottom: 6px;">Số điện thoại *</label>
          <input id="swalSdt" class="swal2-input" placeholder="Sđt khách hàng" style="margin:0;">
          <small id="errSdt" class="error-text"></small>
        </div>
      </div>
    `,
    showCancelButton: true,
    confirmButtonText: "Thêm mới",
    confirmButtonColor: "#ffc107",
    width: "620px",

    // ===========================
    // 🔥 CHỈ SỬA ĐOẠN NÀY
    // ===========================
    preConfirm: async () => {
      const hoTen = document.getElementById("swalHoTen").value.trim();
      const sdt = document.getElementById("swalSdt").value.trim();

      const errHoTen = document.getElementById("errHoTen");
      const errSdt = document.getElementById("errSdt");

      errHoTen.innerText = "";
      errSdt.innerText = "";

      let hasError = false;

      // --- FE validate ---
      if (!hoTen) {
        errHoTen.innerText = "Vui lòng nhập tên khách hàng.";
        hasError = true;
      }

      if (!sdt) {
        errSdt.innerText = "Vui lòng nhập số điện thoại.";
        hasError = true;
      } else if (sdt.length !== 10 || !/^\d+$/.test(sdt)) {
        errSdt.innerText = "Số điện thoại phải gồm 10 số.";
        hasError = true;
      }

      if (hasError) return false;

      // --- Gọi API trong preConfirm ---
      try {
        const res = await themKhachHangMoi({ hoTen, sdt });

        const newKH = res.data;

        await capNhatKhachHang(hoaDon.value.id, newKH.id);
        hoaDon.value.khachHang = newKH;

        notify.success("Thêm mới và gán Khách hàng thành công!");

        return true; // Đóng popup
      } catch (error) {
        console.error("Lỗi thêm nhanh KH:", error);

        const errors = error.response?.data?.errors;

        // --- Lỗi BE từng trường ---
        if (errors) {
          if (errors.hoTen) errHoTen.innerText = errors.hoTen;
          if (errors.sdt) errSdt.innerText = errors.sdt;
        } else {
          // --- Lỗi 400 nhưng không có errors ---
          errHoTen.innerText = error.response?.data || "Lỗi hệ thống!";
        }

        return false; // Giữ popup lại, KHÔNG đóng
      }
    },
  });
};

if (savedList) {
  try {
    hoaDonChoList.value = JSON.parse(savedList) || [];
  } catch (e) {
    hoaDonChoList.value = [];
  }
}

if (savedSelectedId) {
  selectedHoaDonId.value = savedSelectedId;

  const hd = hoaDonChoList.value.find((h) => h.id === savedSelectedId);
  if (hd) {
    hoaDon.value = hd;
    gioHang.value = Array.isArray(hd.sanPhamList) ? hd.sanPhamList : [];
  } else {
    hoaDon.value = null;
    gioHang.value = [];
  }
}

watch(
  hoaDonChoList,
  (newValue) => {
    localStorage.setItem("hoaDonChoList", JSON.stringify(newValue || []));
  },
  { deep: true }
);

watch(selectedHoaDonId, (newValue) => {
  if (newValue) localStorage.setItem("selectedHoaDonId", newValue);
  else localStorage.removeItem("selectedHoaDonId");
});

// hủy hóa đơn
const handleHuyHoaDon = async (id) => {
  localStorage.setItem("hoaDonChoList", JSON.stringify(hoaDonChoList.value));
  localStorage.setItem("selectedHoaDonId", selectedHoaDonId.value);

  const confirm = await Swal.fire({
    title: "Hủy hóa đơn này?",
    text: "Sau khi hủy, hóa đơn sẽ không còn trong danh sách chờ.",
    icon: "warning",
    showCancelButton: true,
    confirmButtonText: "Hủy hóa đơn",
    cancelButtonText: "Đóng",
  });
  if (!confirm.isConfirmed) return;

  try {
    await apiHuyHoaDon(id);
    hoaDonChoList.value = hoaDonChoList.value.filter((hd) => hd.id !== id);
    if (selectedHoaDonId.value === id) {
      selectedHoaDonId.value = null;
      hoaDon.value = null;
      gioHang.value = [];
      tongTienHang.value = 0;
    }
    notify.success("Hóa đơn đã được hủy thành công!");
  } catch (err) {
    console.error(err);
    notify.error("Không thể hủy hóa đơn!");
  }
};

// thêm sản phẩm vào hóa đơn
const handleThemSanPham = async (sp) => {
  if (!hoaDon.value) return notify.warning("Chưa chọn hóa đơn!");
  try {
    const res = await themSanPhamVaoHoaDon(hoaDon.value.id, sp.id, 1);
    const chiTietHDMoi = res.data;
    const chiTietGioHang = {
      ...chiTietHDMoi,
      idChiTietSP: sp.id,
      tenSanPham: sp.tenSanPham,
      mauSac: sp.mauSac,
      kichThuoc: sp.kichThuoc,
      hinhAnhUrl: sp.hinhAnhUrl,
      tenXuatXu: sp.tenXuatXu,
    };
    const indexGioHang = gioHang.value.findIndex(
      (item) => item.id === chiTietGioHang.id
    );

    if (indexGioHang !== -1) {
      gioHang.value[indexGioHang] = chiTietGioHang;
    } else {
      gioHang.value.push(chiTietGioHang);
    }

    hoaDon.value.sanPhamList = gioHang.value;

    const newTongSoLuong = gioHang.value.reduce(
      (sum, item) => sum + (item.soLuong || 0),
      0
    );
    const indexHoaDonCho = hoaDonChoList.value.findIndex(
      (hd) => hd.id === hoaDon.value.id
    );

    if (indexHoaDonCho !== -1) {
      hoaDonChoList.value[indexHoaDonCho].soLuong = newTongSoLuong;
    }

    notify.success("Đã thêm sản phẩm!");
  } catch (err) {
    // Kiểm tra nếu BE trả về thông báo cụ thể
    const msg =
      err.response?.data?.message || // nếu BE có gửi message field
      err.response?.data || // nếu BE trả thẳng message string
      "Không thể thêm sản phẩm!";

    if (msg.includes("Số lượng tồn không đủ")) {
      notify.warning(msg);
    } else {
      notify.error(msg);
    }
  }
};

// API CẬP NHẬT SỐ LƯỢNG SẢN PHẨM TRONG HÓA ĐƠN
const handleCapNhatSoLuong = async (idChiTietHoaDon, newSoLuongRaw) => {
  if (!hoaDon.value) return notify.warning("Chưa chọn hóa đơn!");

  // 1. Chuẩn hóa đầu vào
  let newSoLuong = parseInt(newSoLuongRaw, 10);
  if (isNaN(newSoLuong) || newSoLuong < 1) {
    newSoLuong = 1; // Đảm bảo số lượng luôn >= 1
  }

  // 2. Tìm sản phẩm trong giỏ hàng và xác định Delta
  const spGioHangIndex = gioHang.value.findIndex(
    (item) => item.id === idChiTietHoaDon
  );
  if (spGioHangIndex === -1)
    return notify.error("Sản phẩm không có trong giỏ hàng!");

  const currentSp = gioHang.value[spGioHangIndex];
  const oldSoLuong = currentSp.soLuong;

  // 🔥 TÍNH TOÁN SỐ LƯỢNG CẦN THAY ĐỔI (DELTA)
  const soLuongThayDoi = newSoLuong - oldSoLuong;

  if (soLuongThayDoi === 0) {
    return; // Không làm gì nếu số lượng không đổi
  }

  // Lấy ID chi tiết sản phẩm (ID trong kho) - dùng để gọi API
  const idChiTietSanPhamKho = currentSp.idChiTietSP;

  if (!idChiTietSanPhamKho) {
    return notify.error("Thiếu ID chi tiết sản phẩm để cập nhật!");
  }

  // 🔥 CẬP NHẬT TẠM THỜI TRÊN FE TRƯỚC (Optimistic Update)
  // Điều này giúp giao diện phản hồi nhanh và giảm khả năng xung đột sự kiện
  const tempUpdatedSp = {
    ...currentSp,
    soLuong: newSoLuong,
    // Cập nhật tạm thời thành tiền (để UI phản hồi)
    thanhTien: currentSp.donGia * newSoLuong,
  };

  // Tạo bản sao của gioHang và cập nhật
  const newGioHang = [...gioHang.value];
  newGioHang[spGioHangIndex] = tempUpdatedSp;
  gioHang.value = newGioHang;
  hoaDon.value.sanPhamList = newGioHang;

  // Cập nhật tổng số lượng trên hóa đơn chờ (để UI phản hồi)
  const newTongSoLuong = newGioHang.reduce(
    (sum, item) => sum + (item.soLuong || 0),
    0
  );
  const indexHoaDonCho = hoaDonChoList.value.findIndex(
    (hd) => hd.id === hoaDon.value.id
  );

  if (indexHoaDonCho !== -1) {
    hoaDonChoList.value[indexHoaDonCho].soLuong = newTongSoLuong;
  }

  try {
    // 3. Gọi API cập nhật:
    // ⚠️ DÙNG HÀM TẠO SẢN PHẨM nhưng truyền vào ID sản phẩm và TỔNG SỐ LƯỢNG MỚI
    const res = await themSanPhamVaoHoaDon(
      hoaDon.value.id,
      idChiTietSanPhamKho,
      soLuongThayDoi // 🔥 TRUYỀN TỔNG SỐ LƯỢNG MỚI VÀO API
    );

    const chiTietHDMoi = res.data;

    console.log("✅ API Cập nhật thành công. Dữ liệu BE trả về:", chiTietHDMoi);
    console.log("Số lượng BE báo là:", chiTietHDMoi.soLuong);

    // 4. Cập nhật lại trạng thái FE bằng dữ liệu chính xác từ BE
    // (Nếu BE trả về soLuong và thanhTien chính xác)
    const finalUpdatedSp = {
      // Giữ lại các thuộc tính FE (tên, màu, size, hình ảnh...)
      idChiTietSP: currentSp.idChiTietSP,
      tenSanPham: currentSp.tenSanPham,
      mauSac: currentSp.mauSac,
      kichThuoc: currentSp.kichThuoc,
      hinhAnhUrl: currentSp.hinhAnhUrl,
      tenXuatXu: currentSp.tenXuatXu,

      // Cập nhật các trường số liệu và ID chính thức từ BE
      id: chiTietHDMoi.id, // ID chi tiết hóa đơn
      soLuong: chiTietHDMoi.soLuong, // SỐ LƯỢNG MỚI TỪ BE (Phải khớp với newSoLuong)
      donGia: chiTietHDMoi.donGia,
      thanhTien: chiTietHDMoi.thanhTien,
    };

    // Cần đảm bảo cập nhật lại mảng gioHang.value[spGioHangIndex]
    // Kể cả khi API trả về cùng một giá trị, đây là cách chuẩn để đảm bảo tính nhất quán.
    gioHang.value[spGioHangIndex] = finalUpdatedSp;

    const newTongSoLuongFinal = gioHang.value.reduce(
      (sum, item) => sum + (item.soLuong || 0),
      0
    );
    const indexHoaDonCho = hoaDonChoList.value.findIndex(
      (hd) => hd.id === hoaDon.value.id
    );

    if (indexHoaDonCho !== -1) {
      hoaDonChoList.value[indexHoaDonCho].soLuong = newTongSoLuongFinal;
    }

    notify.success(`Cập nhật số lượng thành công: ${newSoLuong}!`);
  } catch (err) {
    // 5. Xử lý lỗi (Rollback)
    const msg =
      err.response?.data?.message ||
      err.response?.data ||
      "Không thể cập nhật số lượng!";

    // 🔥 Khôi phục số lượng trên FE về giá trị cũ (trước khi gọi API)
    const rolledBackSp = {
      ...currentSp,
      soLuong: oldSoLuong,
      thanhTien: currentSp.donGia * oldSoLuong, // Khôi phục thành tiền
    };
    gioHang.value[spGioHangIndex] = rolledBackSp;
    hoaDon.value.sanPhamList = gioHang.value;

    // Cập nhật tổng số lượng trên hóa đơn chờ về giá trị cũ
    const oldTongSoLuong = gioHang.value.reduce(
      (sum, item) => sum + (item.soLuong || 0),
      0
    );
    const indexHoaDonCho = hoaDonChoList.value.findIndex(
      (hd) => hd.id === hoaDon.value.id
    );

    if (indexHoaDonCho !== -1) {
      hoaDonChoList.value[indexHoaDonCho].soLuong = oldTongSoLuong;
    }

    if (msg.includes("Số lượng tồn không đủ")) {
      notify.warning(msg);
    } else {
      notify.error(msg);
    }
  }
};

// xóa sản phẩm khỏi hóa đơn
const handleXoaSanPham = async (idSp) => {
  if (!hoaDon.value) return;

  console.log("🧩 Gửi xóa sản phẩm:", {
    idHoaDon: hoaDon.value.id,
    idChiTietSanPham: idSp,
  });

  try {
    await xoaSanPhamKhoiHoaDon(hoaDon.value.id, idSp);

    gioHang.value = gioHang.value.filter((item) => item.id !== idSp);
    hoaDon.value.sanPhamList = gioHang.value;

    const newTongSoLuong = gioHang.value.reduce(
      (sum, item) => sum + (item.soLuong || 0),
      0
    );

    const indexHoaDonCho = hoaDonChoList.value.findIndex(
      (hd) => hd.id === hoaDon.value.id
    );

    if (indexHoaDonCho !== -1) {
      hoaDonChoList.value[indexHoaDonCho].soLuong = newTongSoLuong;
    }

    notify.success("Đã xóa sản phẩm!");
  } catch (err) {
    console.error("❌ Xóa thất bại:", err);
    notify.error("Không thể xóa sản phẩm!");
  }
};

// update khách hàng
const handleCapNhatKhachHang = async (khachHang) => {
  if (!hoaDon.value) return;
  try {
    await capNhatKhachHang(hoaDon.value.id, khachHang);
    hoaDon.value.khachHang = khachHang;
    notify.success("Cập nhật khách hàng thành công!");
  } catch (err) {
    notify.error("Không thể cập nhật khách hàng!");
  }
};

const soTienGiamGia = ref(0); // Bắt đầu bằng 0, sau này sẽ là kết quả của việc áp dụng mã giảm giá

// ... (sau hàm tongTien computed)

// 💡 Tính Tổng tiền Hàng (Tổng tiền cũ của bạn)
const tongTienHang = computed(() => {
  if (!gioHang.value || gioHang.value.length === 0) {
    return 0;
  } // Đảm bảo các thuộc tính (thanhTien) là số trước khi tính tổng
  return gioHang.value.reduce((sum, p) => sum + (p.thanhTien || 0), 0);
});

// 💡 Tính Tổng tiền Sau Giảm
const tongTienSauGiam = computed(() => {
  // Tổng tiền hàng - Số tiền giảm (Đảm bảo không âm)
  const result = tongTienHang.value - soTienGiamGia.value;
  return Math.max(0, result);
});

const PHUONG_THUC_ID_MAP = {
  TIEN_MAT: "145B12D7-25E0-4B1A-AC21-CD64328FD446",
  CHUYEN_KHOAN: "B6A1BBF4-E9DF-4C88-90F9-C89599679FDC",
  CA_HAI: "AF15E02B-80D8-41CA-9C8C-D3ECB0B290C7",
};
const phuongThucThanhToan = ref("TIEN_MAT"); // Mặc định là Tiền mặt

const handleChonPhuongThuc = (phuongThuc) => {
  phuongThucThanhToan.value = phuongThuc;
  notify.info(`Đã chọn thanh toán bằng: ${phuongThuc}`);
};

const isVnpayProcessing = ref(false);
const handleVNPayPayment = async () => {
  if (!hoaDon.value) return notify.warning("Chưa có hóa đơn!");
  if (tongTienSauGiam.value <= 0)
    return notify.warning("Tổng tiền phải lớn hơn 0 để thanh toán !");

  isVnpayProcessing.value = true;

  let orderInfoRaw = `Thanh toan HD ${hoaDon.value.ma || hoaDon.value.id}`;
  let orderInfoClean = orderInfoRaw.replace(/\s/g, "");

  const paymentData = {
    amount: tongTienSauGiam.value,
    orderInfo: orderInfoClean,
    language: "vn",
  };

  try {
    const response = await axios.post(
      "http://localhost:8080/admin/vnpay/create-payment",
      paymentData
    );

    const { data, code } = response.data;

    if (code === "00" && data) {
      window.location.href = data;
    } else {
      notify.error(
        `Lỗi tạo giao dịch: ${
          response.data.message || "Không nhận được URL hợp lệ!"
        }`
      );
    }
  } catch (error) {
    console.error("Lỗi khi tạo thanh toán VNPay:", error);
    notify.error("Lỗi kết nối Server khi tạo giao dịch VNPay.");
  } finally {
    isVnpayProcessing.value = false;
  }
};

const handleOpenModalDiaChi = () => {
  // Kiểm tra xem đã có khách hàng chưa
  if (!hoaDon.value?.khachHang) {
    notify.warning("Vui lòng chọn khách hàng trước!");
    return;
  }

  // Kiểm tra xem khách hàng có danh sách địa chỉ không
  const listDiaChi = hoaDon.value.khachHang.danhSachDiaChi || [];

  if (listDiaChi.length === 0) {
    notify.warning("Khách hàng này chưa lưu địa chỉ nào!");
    return;
  }

  // Mở modal
  showDiaChiModal.value = true;
};

// --- 3. Hàm chọn địa chỉ từ Modal ---
const handleChonDiaChiTuModal = (diaChi) => {
  thongTinNguoiNhan.value.tinhThanh = diaChi.thanhPho || "";
  thongTinNguoiNhan.value.quanHuyen = diaChi.huyen || "";
  thongTinNguoiNhan.value.phuongXa = diaChi.xa || "";
  thongTinNguoiNhan.value.diaChiCuThe = diaChi.diaChiCuThe || "";

  // 🔥 THÊM DÒNG NÀY
  syncAddressToSelects();

  showDiaChiModal.value = false;
  notify.success("Đã thay đổi địa chỉ giao hàng!");
};

// thanh toán
const handleThanhToan = async () => {
  // 1. Validate cơ bản
  if (!hoaDon.value) return notify.warning("Chưa có hóa đơn!");
  if (gioHang.value.length === 0) return notify.warning("Giỏ hàng rỗng!");

  const selectedPtttCode = phuongThucThanhToan.value;

  // ---------------------------------------------------------
  // BƯỚC 2: Chuẩn bị dữ liệu Giao Hàng & Loại hóa đơn
  // (Làm bước này trước để có dữ liệu dùng cho việc hiển thị Swal hoặc gửi API)
  // ---------------------------------------------------------
  let thongTinGiaoHang = null;
  let loaiHoaDonQuyetDinh = "Tại cửa hàng"; // Mặc định là tại quầy

  if (isBanGiaoHang.value) {
    // Validate dữ liệu nếu là giao hàng
    if (
      !thongTinNguoiNhan.value.hoTen ||
      !thongTinNguoiNhan.value.sdt ||
      !thongTinNguoiNhan.value.diaChiCuThe
    ) {
      return notify.warning("Vui lòng điền đủ Tên, SĐT và Địa chỉ nhận hàng!");
    }

    loaiHoaDonQuyetDinh = "Online";

    // Ghép chuỗi địa chỉ đầy đủ
    const diaChiDayDu = [
      thongTinNguoiNhan.value.diaChiCuThe,
      thongTinNguoiNhan.value.phuongXa,
      thongTinNguoiNhan.value.quanHuyen,
      thongTinNguoiNhan.value.tinhThanh,
    ]
      .filter(Boolean)
      .join(", ");

    // Đóng gói object giao hàng
    thongTinGiaoHang = {
      tenNguoiNhan: thongTinNguoiNhan.value.hoTen,
      sdt: thongTinNguoiNhan.value.sdt,
      diaChiNguoiNhan: diaChiDayDu,
      phiShip: 0, // Hoặc biến phiVanChuyen nếu có
    };
  }

  // ---------------------------------------------------------
  // BƯỚC 3: Xử lý VNPay (Chuyển khoản)
  // ---------------------------------------------------------
  if (selectedPtttCode === "CHUYEN_KHOAN") {
    // Lưu ý: Nếu muốn lưu địa chỉ giao hàng trước khi sang VNPay,
    // cần gọi API update hóa đơn ở đây trước khi gọi handleVNPayPayment
    await handleVNPayPayment();
    return;
  }

  // ---------------------------------------------------------
  // BƯỚC 4: Xử lý Tiền mặt / Cả hai
  // ---------------------------------------------------------
  const idPhuongThucThanhToan = PHUONG_THUC_ID_MAP[selectedPtttCode];

  if (!idPhuongThucThanhToan) {
    return notify.error("Phương thức thanh toán không hợp lệ!");
  }

  // Hiển thị Confirm
  const confirm = await Swal.fire({
    title: "Xác nhận Thanh toán?",
    html: `
        <div class="text-start">
            <p>Tổng tiền: <strong class="text-danger">${(
              tongTienSauGiam.value ?? 0
            ).toLocaleString()} ₫</strong></p>
            <p>Loại đơn: <strong>${loaiHoaDonQuyetDinh}</strong></p>
            ${
              isBanGiaoHang.value
                ? `<p class="small text-muted">Người nhận: ${thongTinNguoiNhan.value.hoTen} - ${thongTinNguoiNhan.value.sdt}</p>`
                : ""
            }
        </div>
    `,
    icon: "question",
    showCancelButton: true,
    cancelButtonText: "Hủy",
    confirmButtonText: "Xác nhận",
    reverseButtons: true,
  });

  if (!confirm.isConfirmed) {
    return;
  }

  try {
    // ---------------------------------------------------------
    // BƯỚC 5: Gửi dữ liệu xuống Service
    // ---------------------------------------------------------

    // Tạo object chứa tất cả thông tin cần update lúc thanh toán
    const requestData = {
      idPhuongThucThanhToan: idPhuongThucThanhToan,
      loaiHoaDon: loaiHoaDonQuyetDinh,
      ...thongTinGiaoHang, // Spread dữ liệu ship vào (nếu null thì bỏ qua)
    };

    console.log("Dữ liệu gửi thanh toán:", requestData);

    // Gọi API
    await thanhToan(hoaDon.value.id, requestData);

    // Xử lý sau khi thành công
    const completedHoaDonId = hoaDon.value.id;
    notify.success("Thanh toán thành công! Đang chuyển hướng...");

    // Xóa khỏi danh sách chờ
    hoaDonChoList.value = hoaDonChoList.value.filter(
      (hd) => hd.id !== hoaDon.value.id
    );

    // Reset state
    hoaDon.value = null;
    gioHang.value = [];
    selectedHoaDonId.value = null;

    // Chuyển trang
    router.push({ name: "ChiTietHD", params: { id: completedHoaDonId } });
  } catch (err) {
    console.error("Lỗi thanh toán:", err);
    notify.error(
      err.response?.data?.message ||
        err.response?.data ||
        "Thanh toán thất bại! Lỗi Server."
    );
  }
};

// load danh sách sản phẩm (fake data hoặc từ API)
const loadSanPham = async () => {
  try {
    const res = await getDanhSachSanPham();
    console.log(
      "👉 Dữ liệu sản phẩm chi tiết:",
      JSON.parse(JSON.stringify(filteredSanPham.value))
    );
    danhSachSanPham.value = res.data || [];
    filterSanPham();
    if (filteredSanPham.value.length > 0) {
      console.log(
        "Dữ liệu sản phẩm đầu tiên (Kiểm tra URL ảnh):",
        filteredSanPham.value[0]
      );
    }
  } catch (err) {
    console.error("Lỗi khi load sản phẩm:", err);
    danhSachSanPham.value = [];
    filteredSanPham.value = [];
  }
};

loadSanPham();
</script>

<style scoped>
.card {
  background-color: #f8f9fa;
  border: 1px solid #ddd;
}
.search-input::placeholder {
  color: #999;
  opacity: 0.8;
  font-style: italic;
}
.empty-icon {
  width: 60px;
  height: 60px;
  background-color: #ffc107; /* màu xanh dịu */
}
/* Card to (cao bằng 2 card nhỏ bên trái) */
.big-card {
  height: calc(
    (100% - 1rem) * 2 / 3
  ); /* Tự động tính cao bằng 2/3 của cột trái */
}

.card:hover {
  box-shadow: 0 4px 10px rgba(0, 0, 0, 0.1);
  transform: translateY(-2px);
  transition: all 0.2s ease;
}

/* Nếu muốn cố định chiều cao cho các card nhỏ để dễ nhìn */
.col-md-6 .card {
  height: auto;
}
.qr-btn {
  height: 100%; /* Cùng chiều cao với ô input */
  white-space: nowrap; /* Không xuống dòng */
  font-size: 0.9rem; /* Nhỏ hơn một chút cho gọn */
  padding: 0 10px; /* Giảm padding ngang */
}
.nav-tabs .nav-link.active {
  background-color: #ffc107;
  color: white;
}
.nav-tabs .nav-link {
  border-radius: 6px 6px 0 0;
}
.hoa-don-container {
  display: flex;
  gap: 12px;
  overflow-x: auto;
  padding-bottom: 8px;
  scrollbar-width: thin;
}

.hoa-don-container::-webkit-scrollbar {
  height: 6px;
}

.hoa-don-container::-webkit-scrollbar-thumb {
  background: #bbb;
  border-radius: 3px;
}

.hoa-don-card {
  flex: 0 0 180px; /* cố định kích thước mỗi card */
  min-height: 90px;
  background: #fff;
  border: 1px solid #ddd;
  cursor: pointer;
  transition: all 0.2s ease;
}

.hoa-don-card:hover {
  transform: translateY(-2px);
  box-shadow: 0 2px 6px rgba(0, 0, 0, 0.1);
}

.hoa-don-card .badge {
  font-size: 0.7rem;
  padding: 4px 6px;
  letter-spacing: 0.3px;
}
.product-thumb {
  width: 60px; /* Chiều rộng cố định */
  height: 60px; /* Chiều cao cố định */
  object-fit: cover; /* Đảm bảo ảnh không bị méo */
  border-radius: 4px;
  border: 1px solid #ddd;
}
/* CSS cho hình ảnh sản phẩm trong Giỏ hàng (Card 2) */
.cart-thumb {
  width: 150px;
  height: 150px;
  object-fit: cover;
  border-radius: 4px;
  border: 1px solid #e0e0e0;
}
</style>
