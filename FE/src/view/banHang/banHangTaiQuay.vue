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
                    <div class="text-muted mb-3">
                      <span class="badge bg-primary p-2 fs-6 me-2">
                        <i class="fas fa-palette me-1"></i>
                        {{ sp.mauSac || "N/A" }}
                      </span>

                      <span class="badge bg-info text-dark p-2 fs-6 me-2">
                        <i class="fas fa-globe me-1"></i>
                        {{ sp.tenXuatXu || "N/A" }}
                      </span>

                      <span class="badge bg-warning text-dark p-2 fs-6 me-2">
                        <i class="fas fa-ruler me-1"></i>
                        {{ sp.kichThuoc || "N/A" }}
                      </span>
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
                            max-width: 55px;
                            font-size: 0.9rem;
                            height: 28px;
                          "
                          @input="
                            handleUpdateTempSoLuong(sp.id, $event.target.value)
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
                  <label class="form-label text-start w-100"
                    >Tên người nhận</label
                  >
                  <input
                    type="text"
                    class="form-control"
                    v-model="thongTinNguoiNhan.hoTen"
                    placeholder="Nhập tên..."
                  />
                </div>

                <div class="col-md-6">
                  <label class="form-label text-start w-100"
                    >Số điện thoại</label
                  >
                  <input
                    type="tel"
                    class="form-control"
                    v-model="thongTinNguoiNhan.sdt"
                    placeholder="Nhập SĐT..."
                  />
                </div>

                <div class="col-md-6">
                  <label class="form-label w-100 small fw-bold"
                    >Tỉnh/Thành phố</label
                  >
                  <select
                    class="form-select"
                    v-model="provinceCode"
                    @change="handleProvinceChange"
                  >
                    <option :value="null">Chọn Tỉnh/Thành</option>
                    <option
                      v-for="p in provinces"
                      :key="p.ProvinceID"
                      :value="p.ProvinceID"
                    >
                      {{ p.ProvinceName }}
                    </option>
                  </select>
                </div>

                <div class="col-md-6">
                  <label class="form-label w-100 small fw-bold"
                    >Quận/Huyện</label
                  >
                  <select
                    class="form-select"
                    v-model="districtCode"
                    @change="handleDistrictChange"
                    :disabled="!provinceCode"
                  >
                    <option :value="null">Chọn Quận/Huyện</option>
                    <option
                      v-for="d in districts"
                      :key="d.DistrictID"
                      :value="d.DistrictID"
                    >
                      {{ d.DistrictName }}
                    </option>
                  </select>
                </div>

                <div class="col-md-6">
                  <label class="form-label w-100 small fw-bold"
                    >Phường/Xã</label
                  >
                  <select
                    class="form-select"
                    v-model="wardCode"
                    @change="handleWardChange"
                    :disabled="!districtCode"
                  >
                    <option :value="null">Chọn Phường/Xã</option>
                    <option
                      v-for="w in wards"
                      :key="w.WardCode"
                      :value="w.WardCode"
                    >
                      {{ w.WardName }}
                    </option>
                  </select>
                </div>

                <div class="col-12">
                  <label class="form-label w-100 small fw-bold"
                    >Địa chỉ cụ thể</label
                  >
                  <input
                    type="text"
                    class="form-control"
                    v-model="thongTinNguoiNhan.diaChiCuThe"
                    placeholder="Số nhà, ngõ, tên đường..."
                  />
                </div>

                <div class="col-12 mt-3">
                  <div
                    class="d-flex justify-content-between align-items-center p-3 border rounded shadow-sm"
                    style="
                      background-color: #f8f9fa;
                      border-color: #e0e0e0 !important;
                    "
                  >
                    <div class="d-flex align-items-center">
                      <img
                        src="https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcSMVcYERbmgDtRbwTfiuNj8HZPRXo0rUtsOlg&s"
                        alt="GHN Express"
                        style="
                          height: 50px;
                          width: auto;
                          object-fit: contain;
                          margin-right: 12px;
                        "
                      />
                      <div class="d-flex flex-column text-start">
                        <span class="small text-muted fw-bold"
                          >Đơn vị vận chuyển</span
                        >
                        <span class="fw-bold text-primary"
                          >Giao Hàng Nhanh</span
                        >
                      </div>
                    </div>
                    <div class="text-end">
                      <span class="d-block small text-muted">Phí ship</span>
                      <span class="fw-bolder fs-5 text-success">
                        {{ formatCurrency(phiShip) }}
                      </span>
                    </div>
                  </div>
                </div>
              </div>
            </div>

            <div class="mb-3 text-start border-bottom pb-3">
              <div
                v-if="hoaDon && hoaDon.phieuGiamGia && hoaDon.phieuGiamGia.id"
                class="mb-3 text-start border-bottom pb-3"
              >
                <div
                  class="d-flex justify-content-between align-items-center mb-2"
                >
                  <span class="fw-bold small text-uppercase text-muted">
                    <i class="fa-solid fa-ticket text-warning me-1"></i> Mã giảm
                    giá
                  </span>
                </div>

                <div
                  class="card shadow-sm border-0"
                  style="
                    background-color: #f0fdf4;
                    border-left: 4px solid #198754 !important;
                  "
                >
                  <div class="card-body p-2">
                    <div
                      class="d-flex justify-content-between align-items-center"
                    >
                      <div class="d-flex align-items-center">
                        <div class="me-2 text-success">
                          <i class="fa-solid fa-gift fa-lg"></i>
                        </div>
                        <div class="lh-1">
                          <div class="fw-bold text-dark mb-1">
                            {{ hoaDon.phieuGiamGia.ten || "Mã giảm giá" }}
                          </div>
                          <small
                            class="text-muted bg-white px-1 border rounded font-monospace"
                          >
                            {{ hoaDon.phieuGiamGia.ma || "CODE" }}
                          </small>
                        </div>
                      </div>

                      <div class="text-end">
                        <span class="fw-bold text-danger fs-6">
                          -
                          {{
                            formatCurrency(
                              tongTienHang -
                                (hoaDon.tongTienSauGiam || tongTienHang)
                            )
                          }}
                        </span>
                        <br />
                        <small
                          v-if="hoaDon.phieuGiamGia.hinhThucGiamGia"
                          class="text-success fst-italic"
                          style="font-size: 0.7rem"
                        >
                          (Giảm {{ Number(hoaDon.phieuGiamGia.giaTriGiam) }}%)
                        </small>
                      </div>
                    </div>

                    <div
                      class="mt-2 pt-2 border-top border-success border-opacity-25 d-flex justify-content-between small text-muted"
                    >
                      <span>
                        <i class="fa-solid fa-sack-dollar"></i> Đơn tối thiểu:
                        <strong>{{
                          formatCurrency(hoaDon.phieuGiamGia.giaTriGiamToiThieu)
                        }}</strong>
                      </span>
                      <span>
                        HSD: {{ formatDate(hoaDon.phieuGiamGia.ngayKetThuc) }}
                      </span>
                    </div>
                  </div>
                </div>
              </div>

              <div v-else class="mb-3 text-start border-bottom pb-3">
                <div
                  class="d-flex justify-content-between align-items-center mb-2"
                >
                  <span class="fw-bold small text-uppercase text-muted"
                    >Mã giảm giá</span
                  >
                  <button
                    class="btn btn-sm btn-outline-primary"
                    @click="handleApDungKhuyenMai"
                    :disabled="
                      !hoaDon ||
                      !hoaDon.khachHang ||
                      hoaDon.khachHang.hoTen === 'Khách lẻ'
                    "
                  >
                    <i class="fa-solid fa-ticket me-1"></i> Chọn mã
                  </button>
                </div>
                <div class="text-end small text-muted fst-italic">
                  {{
                    !hoaDon ||
                    !hoaDon.khachHang ||
                    hoaDon.khachHang.hoTen === "Khách lẻ"
                      ? "*Chọn khách hàng thành viên"
                      : "Chưa áp dụng mã nào"
                  }}
                </div>
              </div>

              <div
                v-if="
                  hoaDon &&
                  (!hoaDon.khachHang || hoaDon.khachHang.hoTen === 'Khách lẻ')
                "
                class="text-muted small fst-italic text-end"
              >
                <i class="fa-solid fa-circle-info me-1"></i>
                *Chọn khách hàng thành viên để dùng mã
              </div>
            </div>

            <div class="border rounded p-2 bg-light-subtle mb-3">
              <div class="d-flex justify-content-between mb-1">
                <span class="text-muted">Tổng tiền hàng:</span>
                <span class="fw-bold text-dark">
                  {{ formatCurrency(tongTienHang) }}
                </span>
              </div>

              <div
                v-if="
                  hoaDon &&
                  hoaDon.phieuGiamGia &&
                  tongTienHang - hoaDon.tongTienSauGiam > 0
                "
                class="d-flex justify-content-between mb-1 text-success"
              >
                <span> <i class="fa-solid fa-gift me-1"></i> Khuyến mãi: </span>
                <span class="fw-bold">
                  - {{ formatCurrency(tongTienHang - hoaDon.tongTienSauGiam) }}
                </span>
              </div>

              <div
                v-if="isBanGiaoHang"
                class="d-flex justify-content-between mb-1 text-primary"
              >
                <span>Phí vận chuyển:</span>
                <span class="fw-bold">+ {{ formatCurrency(phiShip) }}</span>
              </div>

              <hr class="my-2" />

              <div
                class="d-flex justify-content-between align-items-center pt-1"
              >
                <span class="fw-bold h6 mb-0">KHÁCH CẦN TRẢ:</span>
                <span class="fw-bolder fs-4 text-danger">
                  {{
                    formatCurrency(
                      (hoaDon?.tongTienSauGiam !== null &&
                      hoaDon?.tongTienSauGiam !== undefined
                        ? hoaDon.tongTienSauGiam
                        : tongTienHang) + (isBanGiaoHang ? phiShip : 0)
                    )
                  }}
                </span>
              </div>
            </div>

            <div class="mb-3 text-start">
              <h6 class="fw-bold small text-uppercase text-muted mb-2">
                Phương thức thanh toán
              </h6>

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
                    class="btn w-100 btn-sm"
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
                    class="btn w-100 btn-sm"
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
                    class="btn w-100 btn-sm"
                  >
                    Cả hai
                  </button>
                </div>
              </div>
            </div>

            <button
              class="btn btn-warning w-100 fw-bold py-2 text-uppercase"
              @click="handleThanhToan"
              :disabled="!hoaDon || isVnpayProcessing"
            >
              <span v-if="isVnpayProcessing"
                ><i class="fas fa-spinner fa-spin me-2"></i>Đang xử lý
                VNPay...</span
              >
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
import { ref, computed, watch, onMounted, nextTick } from "vue";
import Breadcrumb from "@/components/common/Breadcrumb.vue";
import { useNotify } from "@/composables/useNotify";

import { useGiaoHang } from "@/composables/banHang/useGiaoHang";
import { useGioHang } from "@/composables/banHang/useGioHang";
import { useHoaDon } from "@/composables/banHang/useHoaDon";
import { useKhachHang } from "@/composables/banHang/useKhachHang";
import { useSanPham } from "@/composables/banHang/useSanPham";
import { useThanhToan } from "@/composables/banHang/useThanhToan";

const notify = useNotify();
const idNhanVien = "02b6c170-6aa5-4cc7-8e52-abc123456789";

const formatCurrency = (amount) => {
  if (amount === null || amount === undefined) return "0 ₫";
  return amount.toLocaleString("vi-VN", { style: "currency", currency: "VND" });
};

// Hàm hiển thị trạng thái (Nguyên nhân gây lỗi của bạn)
const trangThaiText = (value) => {
  const map = {
    0: "Đã hủy",
    1: "Chờ xác nhận",
    2: "Đã xác nhận",
    3: "Chờ thanh toán",
    4: "Đang giao",
    5: "Hoàn thành",
  };
  return map[value] || "Không xác định";
};

// 1. Hóa đơn
const {
  hoaDon,
  hoaDonChoList,
  selectedHoaDonId,
  savedSelectedId,
  handleTaoHoaDon,
  selectHoaDon,
  handleHuyHoaDon,
  handleApDungKhuyenMai,
} = useHoaDon(notify, idNhanVien, () => {
  resetFormGiaoHang();
  resetKhachHangState();
});

// 2. Giỏ hàng
const {
  gioHang,
  tongTienHang,
  tongTienSauGiam,
  soTienGiamGia,
  handleThemSanPham,
  handleUpdateTempSoLuong,
  handleCapNhatSoLuong,
  handleXoaSanPham,
} = useGioHang(notify, hoaDon, hoaDonChoList);

// 3. Giao hàng (Cần truyền hoaDon để check khách hàng khi mở modal)
const {
  isBanGiaoHang,
  phiShip,
  provinces,
  districts,
  wards,
  provinceCode,
  districtCode,
  wardCode,
  thongTinNguoiNhan,
  showDiaChiModal,
  calculateShippingFee,
  handleProvinceChange,
  handleDistrictChange,
  handleWardChange,
  autoFillAddressFromNames,
  resetFormGiaoHang,
  handleOpenModalDiaChi,
  handleChonDiaChiTuModal,
} = useGiaoHang(notify, tongTienHang, hoaDon);

// 4. Khách hàng
const {
  searchKeyword,
  searchResults,
  isGuestEditable,
  showAddGuestButton,
  khachLeMacDinh,
  assignKhachHang,
  handleTimKhachHang,
  handleThemNhanhKhachHang,
  handleSelectKhachHang,
  resetKhachHangState,
} = useKhachHang(
  notify,
  hoaDon,
  thongTinNguoiNhan,
  autoFillAddressFromNames,
  resetFormGiaoHang,
  calculateShippingFee,
  isBanGiaoHang,
  phiShip
);

// 5. Thanh toán (Truyền info ship để lấy dữ liệu)
const {
  phuongThucThanhToan,
  isVnpayProcessing,
  tongTienCanThanhToan,
  handleThanhToan,
} = useThanhToan(
  notify,
  hoaDon,
  gioHang,
  hoaDonChoList,
  selectedHoaDonId,
  tongTienSauGiam,
  isBanGiaoHang,
  phiShip,
  thongTinNguoiNhan
);

// 6. Sản phẩm (Mới)
const {
  filteredSanPham,
  searchSanPham,
  filterMauSac,
  filterKichThuoc,
  filterXuatXu,
  listMauSac,
  listKichThuoc,
  listXuatXu,
  paginatedSanPham,
  currentPage,
  itemsPerPage,
  totalPages,
  goToPage,
  loadSanPham,
  handleValidateSoLuong,
} = useSanPham(notify);

const formatDate = (dateString) => {
  if (!dateString) return "";
  const date = new Date(dateString);
  return date.toLocaleDateString("vi-VN");
};

// Init
onMounted(async () => {
  await loadSanPham();

  if (savedSelectedId && savedSelectedId.value) {
    selectHoaDon(savedSelectedId.value);

    await nextTick();

    if (hoaDon.value && hoaDon.value.khachHang) {
      const isKhachLe = hoaDon.value.khachHang.id === khachLeMacDinh.id;

      await assignKhachHang(hoaDon.value.khachHang, isKhachLe);
    }
  }
});
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
