<script setup></script>
<template>
      <div class="page-inner">
        <div class="page-header">
          <h3 class="fw-bold mb-3">DataTables.Net</h3>
          <ul class="breadcrumbs mb-3">
            <li class="nav-home">
              <a href="#">
                <i class="icon-home"></i>
              </a>
            </li>
            <li class="separator">
              <i class="icon-arrow-right"></i>
            </li>
            <li class="nav-item">
              <a href="#">Tables</a>
            </li>
            <li class="separator">
              <i class="icon-arrow-right"></i>
            </li>
            <li class="nav-item">
              <a href="#">Datatables</a>
            </li>
          </ul>
        </div>
        <div class="row">
          <div class="col-md-12">
            <div class="card">
              <div class="card-header">
                <div class="d-flex align-items-center">
                  <h4 class="card-title">Bộ lọc</h4>
                </div>
              </div>
              <div class="card-body">
                <form>
                  <div class="row g-3">
                    <!-- <div class="col-md-4">
                          <label class="form-label">Tìm kiếm</label>
                          <input
                            type="text"
                            class="form-control"
                            placeholder="Tìm kiếm theo tên sản phẩm..."
                          />
                        </div> -->

                    <div class="col-md-4">
                      <label class="form-label">Thương hiệu</label>
                      <select class="form-select">
                        <option>Tất cả</option>
                        <option>Việt Nam Laforce</option>
                        <option>Tâm anh</option>
                      </select>
                    </div>

                    <div class="col-md-4">
                      <label class="form-label">Xuất xứ</label>
                      <select class="form-select">
                        <option>Việt Nam</option>
                        <option>Trung Quốc</option>
                        <option>Mỹ</option>
                      </select>
                    </div>

                    <div class="col-md-4">
                      <label class="form-label">Đế giày</label>
                      <select class="form-select">
                        <option>Tất cả</option>
                        <option>PVC</option>
                        <option>EVA</option>
                        <option>Cao Su</option>
                      </select>
                    </div>

                    <div class="col-md-4">
                      <label class="form-label">Mục đích sử dụng</label>
                      <select class="form-select">
                        <option>Tất cả</option>
                        <option>Đi làm, công sở</option>
                        <option>Dự tiệc</option>
                        <option>Thể hiện cá tính</option>
                      </select>
                    </div>

                    <div class="col-md-4">
                      <label class="form-label">Trạng thái tồn kho</label>
                      <div class="d-flex mt-2">
                        <div class="form-check me-3">
                          <input
                            class="form-check-input"
                            type="radio"
                            name="status"
                            checked
                          />
                          <label class="form-check-label">Tất cả</label>
                        </div>
                        <div class="form-check me-3">
                          <input
                            class="form-check-input"
                            type="radio"
                            name="status"
                          />
                          <label class="form-check-label">Còn hàng</label>
                        </div>
                        <div class="form-check">
                          <input
                            class="form-check-input"
                            type="radio"
                            name="status"
                          />
                          <label class="form-check-label">Hết hàng</label>
                        </div>
                      </div>
                    </div>
                  </div>

                  <!-- Footer -->
                  <div
                    class="d-flex flex-column flex-md-row justify-content-between align-items-center mt-4"
                  >
                    <p class="mb-2 mb-md-0">
                      Tổng số sản phẩm:
                      <span class="text-success fw-bold">0</span>
                    </p>
                    <div>
                      <!-- <button type="button" class="btn btn-secondary me-2">
                            Tải Excel
                          </button> -->
                      <button type="button" class="btn btn-warning me-2">
                        Thêm chi tiết sản phẩm
                      </button>
                      <button type="reset" class="btn btn-dark">
                        Đặt lại bộ lọc
                      </button>
                    </div>
                  </div>
                </form>
              </div>
            </div>
          </div>

          <div class="col-md-12">
            <div class="card">
              <div class="card-header">
                <div class="d-flex align-items-center">
                  <h4 class="card-title">Danh sách sản phẩm</h4>
                  <!-- <button
                        class="btn btn-primary btn-round ms-auto"
                        data-bs-toggle="modal"
                        data-bs-target="#addRowModal"
                      >
                        <i class="fa fa-plus"></i>
                        Add Row
                      </button> -->
                </div>
              </div>
              <div class="card-body">
                <!-- Modal -->
                <div
                  class="modal fade"
                  id="addRowModal"
                  tabindex="-1"
                  role="dialog"
                  aria-hidden="true"
                >
                  <div class="modal-dialog" role="document">
                    <div class="modal-content">
                      <div class="modal-header border-0">
                        <h5 class="modal-title">
                          <span class="fw-mediumbold"> New</span>
                          <span class="fw-light"> Row </span>
                        </h5>
                        <button
                          type="button"
                          class="close"
                          data-dismiss="modal"
                          aria-label="Close"
                        >
                          <span aria-hidden="true">&times;</span>
                        </button>
                      </div>
                      <div class="modal-body">
                        <p class="small">
                          Create a new row using this form, make sure you fill
                          them all
                        </p>
                        <form>
                          <div class="row">
                            <div class="col-sm-12">
                              <div class="form-group form-group-default">
                                <label>Name</label>
                                <input
                                  id="addName"
                                  type="text"
                                  class="form-control"
                                  placeholder="fill name"
                                />
                              </div>
                            </div>
                            <div class="col-md-6 pe-0">
                              <div class="form-group form-group-default">
                                <label>Position</label>
                                <input
                                  id="addPosition"
                                  type="text"
                                  class="form-control"
                                  placeholder="fill position"
                                />
                              </div>
                            </div>
                            <div class="col-md-6">
                              <div class="form-group form-group-default">
                                <label>Office</label>
                                <input
                                  id="addOffice"
                                  type="text"
                                  class="form-control"
                                  placeholder="fill office"
                                />
                              </div>
                            </div>
                          </div>
                        </form>
                      </div>
                      <div class="modal-footer border-0">
                        <button
                          type="button"
                          id="addRowButton"
                          class="btn btn-primary"
                        >
                          Add
                        </button>
                        <button
                          type="button"
                          class="btn btn-danger"
                          data-dismiss="modal"
                        >
                          Close
                        </button>
                      </div>
                    </div>
                  </div>
                </div>

                <div class="table-responsive">
                  <table
                    id="add-row"
                    class="display table table-striped table-hover"
                  >
                    <thead>
                      <tr>
                        <th>Tên Sản Phẩm</th>
                        <th>Xuất xứ</th>
                        <th>Thương hiệu</th>
                        <th style="width: 10%">Thao tác</th>
                      </tr>
                    </thead>
                    <tfoot>
                      <tr>
                        <th>Tên Sản Phẩm</th>
                        <th>Xuất xứ</th>
                        <th>Thương hiệu</th>
                        <th>Thao tác</th>
                      </tr>
                    </tfoot>
                    <tbody>
                      <tr>
                        <td>
                          Giày tây nam họa tiết Wingtip đế Chunky GNTA20232-D
                        </td>
                        <td>Việt Nam</td>
                        <td>Tâm Anh</td>
                        <td>
                          <div class="form-button-action">
                            <button
                              type="button"
                              data-bs-toggle="tooltip"
                              title=""
                              class="btn btn-link btn-primary btn-lg"
                              data-original-title="Edit Task"
                            >
                              <i class="fa fa-edit"></i>
                            </button>
                            <button
                              type="button"
                              data-bs-toggle="tooltip"
                              title=""
                              class="btn btn-link btn-danger"
                              data-original-title="Remove"
                            >
                              <i class="fa fa-times"></i>
                            </button>
                          </div>
                        </td>
                      </tr>
                      <tr>
                        <td>
                          Giày tây nam họa tiết Wingtip đế Chunky GNTA20232-D
                        </td>
                        <td>Việt Nam</td>
                        <td>Tâm Anh</td>
                        <td>
                          <div class="form-button-action">
                            <button
                              type="button"
                              data-bs-toggle="tooltip"
                              title=""
                              class="btn btn-link btn-primary btn-lg"
                              data-original-title="Edit Task"
                            >
                              <i class="fa fa-edit"></i>
                            </button>
                            <button
                              type="button"
                              data-bs-toggle="tooltip"
                              title=""
                              class="btn btn-link btn-danger"
                              data-original-title="Remove"
                            >
                              <i class="fa fa-times"></i>
                            </button>
                          </div>
                        </td>
                      </tr>
                      <tr>
                        <td>
                          Giày tây nam họa tiết Wingtip đế Chunky GNTA20232-D
                        </td>
                        <td>Việt Nam</td>
                        <td>Tâm Anh</td>
                        <td>
                          <div class="form-button-action">
                            <button
                              type="button"
                              data-bs-toggle="tooltip"
                              title=""
                              class="btn btn-link btn-primary btn-lg"
                              data-original-title="Edit Task"
                            >
                              <i class="fa fa-edit"></i>
                            </button>
                            <button
                              type="button"
                              data-bs-toggle="tooltip"
                              title=""
                              class="btn btn-link btn-danger"
                              data-original-title="Remove"
                            >
                              <i class="fa fa-times"></i>
                            </button>
                          </div>
                        </td>
                      </tr>
                      <tr>
                        <td>
                          Giày tây nam họa tiết Wingtip đế Chunky GNTA20232-D
                        </td>
                        <td>Việt Nam</td>
                        <td>Tâm Anh</td>
                        <td>
                          <div class="form-button-action">
                            <button
                              type="button"
                              data-bs-toggle="tooltip"
                              title=""
                              class="btn btn-link btn-primary btn-lg"
                              data-original-title="Edit Task"
                            >
                              <i class="fa fa-edit"></i>
                            </button>
                            <button
                              type="button"
                              data-bs-toggle="tooltip"
                              title=""
                              class="btn btn-link btn-danger"
                              data-original-title="Remove"
                            >
                              <i class="fa fa-times"></i>
                            </button>
                          </div>
                        </td>
                      </tr>
                      <tr>
                        <td>
                          Giày tây nam họa tiết Wingtip đế Chunky GNTA20232-D
                        </td>
                        <td>Việt Nam</td>
                        <td>Tâm Anh</td>
                        <td>
                          <div class="form-button-action">
                            <button
                              type="button"
                              data-bs-toggle="tooltip"
                              title=""
                              class="btn btn-link btn-primary btn-lg"
                              data-original-title="Edit Task"
                            >
                              <i class="fa fa-edit"></i>
                            </button>
                            <button
                              type="button"
                              data-bs-toggle="tooltip"
                              title=""
                              class="btn btn-link btn-danger"
                              data-original-title="Remove"
                            >
                              <i class="fa fa-times"></i>
                            </button>
                          </div>
                        </td>
                      </tr>
                      <tr>
                        <td>
                          Giày tây nam họa tiết Wingtip đế Chunky GNTA20232-D
                        </td>
                        <td>Việt Nam</td>
                        <td>Tâm Anh</td>
                        <td>
                          <div class="form-button-action">
                            <button
                              type="button"
                              data-bs-toggle="tooltip"
                              title=""
                              class="btn btn-link btn-primary btn-lg"
                              data-original-title="Edit Task"
                            >
                              <i class="fa fa-edit"></i>
                            </button>
                            <button
                              type="button"
                              data-bs-toggle="tooltip"
                              title=""
                              class="btn btn-link btn-danger"
                              data-original-title="Remove"
                            >
                              <i class="fa fa-times"></i>
                            </button>
                          </div>
                        </td>
                      </tr>
                      <tr>
                        <td>Giày Chelsea Boot đế Chunky</td>
                        <td>Việt Nam</td>
                        <td>La Force</td>
                        <td>
                          <div class="form-button-action">
                            <button
                              type="button"
                              data-bs-toggle="tooltip"
                              title=""
                              class="btn btn-link btn-primary btn-lg"
                              data-original-title="Edit Task"
                            >
                              <i class="fa fa-edit"></i>
                            </button>
                            <button
                              type="button"
                              data-bs-toggle="tooltip"
                              title=""
                              class="btn btn-link btn-danger"
                              data-original-title="Remove"
                            >
                              <i class="fa fa-times"></i>
                            </button>
                          </div>
                        </td>
                      </tr>
                      <tr>
                        <td>Giày Chelsea Boot đế Chunky</td>
                        <td>Việt Nam</td>
                        <td>La Force</td>
                        <td>
                          <div class="form-button-action">
                            <button
                              type="button"
                              data-bs-toggle="tooltip"
                              title=""
                              class="btn btn-link btn-primary btn-lg"
                              data-original-title="Edit Task"
                            >
                              <i class="fa fa-edit"></i>
                            </button>
                            <button
                              type="button"
                              data-bs-toggle="tooltip"
                              title=""
                              class="btn btn-link btn-danger"
                              data-original-title="Remove"
                            >
                              <i class="fa fa-times"></i>
                            </button>
                          </div>
                        </td>
                      </tr>
                      <tr>
                        <td>Giày Chelsea Boot đế Chunky</td>
                        <td>Việt Nam</td>
                        <td>La Force</td>
                        <td>
                          <div class="form-button-action">
                            <button
                              type="button"
                              data-bs-toggle="tooltip"
                              title=""
                              class="btn btn-link btn-primary btn-lg"
                              data-original-title="Edit Task"
                            >
                              <i class="fa fa-edit"></i>
                            </button>
                            <button
                              type="button"
                              data-bs-toggle="tooltip"
                              title=""
                              class="btn btn-link btn-danger"
                              data-original-title="Remove"
                            >
                              <i class="fa fa-times"></i>
                            </button>
                          </div>
                        </td>
                      </tr>
                      <tr>
                        <td>Giày Chelsea Boot đế Chunky</td>
                        <td>Việt Nam</td>
                        <td>La Force</td>
                        <td>
                          <div class="form-button-action">
                            <button
                              type="button"
                              data-bs-toggle="tooltip"
                              title=""
                              class="btn btn-link btn-primary btn-lg"
                              data-original-title="Edit Task"
                            >
                              <i class="fa fa-edit"></i>
                            </button>
                            <button
                              type="button"
                              data-bs-toggle="tooltip"
                              title=""
                              class="btn btn-link btn-danger"
                              data-original-title="Remove"
                            >
                              <i class="fa fa-times"></i>
                            </button>
                          </div>
                        </td>
                      </tr>
                    </tbody>
                  </table>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
</template>


