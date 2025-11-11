USE [master];
GO

-- Đóng tất cả các kết nối đến database
ALTER DATABASE WEB_BAN_GIAY SET SINGLE_USER WITH ROLLBACK IMMEDIATE;
GO

IF DB_ID('WEB_BAN_GIAY') IS NOT NULL
    DROP DATABASE WEB_BAN_GIAY;
GO

CREATE DATABASE WEB_BAN_GIAY;
GO

USE WEB_BAN_GIAY;
GO

CREATE TABLE muc_dich_su_dung(
    id UNIQUEIDENTIFIER DEFAULT NEWID() PRIMARY KEY,
    ma NVARCHAR(50) UNIQUE ,
    ten NVARCHAR(200) 
);

CREATE TABLE day_giay(
    id UNIQUEIDENTIFIER DEFAULT NEWID() PRIMARY KEY,
    ma NVARCHAR(50) UNIQUE ,
    ten NVARCHAR(200) 
);

CREATE TABLE de_giay(
    id UNIQUEIDENTIFIER DEFAULT NEWID() PRIMARY KEY,
    ma NVARCHAR(50) UNIQUE ,
    ten NVARCHAR(200) 
);

CREATE TABLE thuong_hieu(
    id UNIQUEIDENTIFIER DEFAULT NEWID() PRIMARY KEY,
    ma NVARCHAR(50) UNIQUE ,
    ten NVARCHAR(200) 
);

CREATE TABLE danh_muc(
    id UNIQUEIDENTIFIER DEFAULT NEWID() PRIMARY KEY,
    ma NVARCHAR(50) UNIQUE ,
    ten NVARCHAR(200) 
);

CREATE TABLE hinh_anh(
    id UNIQUEIDENTIFIER DEFAULT NEWID() PRIMARY KEY,
    ma NVARCHAR(50) UNIQUE ,
    url_anh1 NVARCHAR(100),
    url_anh2 NVARCHAR(100),
    url_anh3 NVARCHAR(100)
);

CREATE TABLE xuat_xu(
    id UNIQUEIDENTIFIER DEFAULT NEWID() PRIMARY KEY,
    ma NVARCHAR(50) UNIQUE ,
    ten NVARCHAR(200) 
);

CREATE TABLE mau_sac(
    id UNIQUEIDENTIFIER DEFAULT NEWID() PRIMARY KEY,
    ma NVARCHAR(50) UNIQUE ,
    ten NVARCHAR(200) 
);

CREATE TABLE kich_thuoc(
    id UNIQUEIDENTIFIER DEFAULT NEWID() PRIMARY KEY,
    ma NVARCHAR(50) UNIQUE ,
    ten NVARCHAR(200) 
);

CREATE TABLE chat_lieu(
    id UNIQUEIDENTIFIER DEFAULT NEWID() PRIMARY KEY,
    ma NVARCHAR(50) UNIQUE ,
    ten NVARCHAR(200) 
);

CREATE TABLE dot_giam_gia(
    id UNIQUEIDENTIFIER DEFAULT NEWID() PRIMARY KEY,
    ma NVARCHAR(50) UNIQUE ,
    ten NVARCHAR(200) ,
    gia_tri_giam INT,
    so_tien_giam_toi_da DECIMAL,
    ngay_bat_dau DATE,
    ngay_ket_thuc DATE,
    trang_thai BIT --0 la ngung hoat dong, 1 la dang hoat dong
);

CREATE TABLE chuc_vu (
    id UNIQUEIDENTIFIER DEFAULT NEWID() PRIMARY KEY,
    ma NVARCHAR(50) UNIQUE ,
    ten NVARCHAR(200) ,
    ngay_tao DATE,
    ngay_cap_nhat DATE,
    mo_ta NVARCHAR(100)
);

INSERT INTO chuc_vu (ma, ten) VALUES
('CV01',N'Quản trị viên'),
('CV02',N'Nhân viên bán hàng'),
('CV03',N'Nhân viên kho');

CREATE TABLE phuong_thuc_thanh_toan (
    id UNIQUEIDENTIFIER DEFAULT NEWID() PRIMARY KEY,
    ma NVARCHAR(50),
    ten NVARCHAR(200)
);

INSERT INTO phuong_thuc_thanh_toan (ma, ten) VALUES
('PT01',N'Tiền mặt'),
('PT02',N'Chuyển khoản');


CREATE TABLE nhan_vien (
    id UNIQUEIDENTIFIER DEFAULT NEWID() PRIMARY KEY,
    id_chuc_vu UNIQUEIDENTIFIER,
    ma NVARCHAR(50) UNIQUE ,
    ho_ten NVARCHAR(200) ,
    sdt NVARCHAR(20),
    email NVARCHAR(200) UNIQUE,
    url_anh NVARCHAR(100),
    gioi_tinh BIT,
    ngay_sinh DATE,
    dia_chi NVARCHAR(100),
    cccd NVARCHAR(20),
    tai_khoan NVARCHAR(100) UNIQUE,
    mat_khau NVARCHAR(200),
    ngay_tao DATE,
    ngay_sua DATE,
    nguoi_tao NVARCHAR(20),
    nguoi_sua NVARCHAR(20), 
    trang_thai TINYINT DEFAULT 1, -- 0 la ngung hoat dong, 1 la dang hoat dong
    FOREIGN KEY (id_chuc_vu) REFERENCES chuc_vu(id)
);

INSERT INTO nhan_vien (ma, ho_ten, email, tai_khoan, mat_khau) VALUES
('NV01',N'Nguyễn Văn A','a@gmail.com','nva','123'),
('NV02',N'Trần Thị B','b@gmail.com','ttb','123');


CREATE TABLE khach_hang (
    id UNIQUEIDENTIFIER DEFAULT NEWID() PRIMARY KEY,
    ma NVARCHAR(50) UNIQUE ,
    ho_ten NVARCHAR(200) ,
    sdt NVARCHAR(20),
    email NVARCHAR(200) UNIQUE,
    url_anh NVARCHAR(100),
    gioi_tinh BIT,
    ngay_sinh DATE,
    dia_chi NVARCHAR(100),
    tai_khoan NVARCHAR(100) UNIQUE,
    mat_khau NVARCHAR(200),
    ngay_tao DATE,
    ngay_sua DATE,
    nguoi_tao NVARCHAR(20),
    nguoi_sua NVARCHAR(20), 
    trang_thai TINYINT DEFAULT 1 -- 0 la ngung hoat dong, 1 la dang hoat dong
);

INSERT INTO khach_hang (ma, ho_ten, email, tai_khoan, mat_khau) VALUES
('KH01',N'Nguyễn Văn C','c@gmail.com','nvc','123'),
('KH02',N'Lê Thị D','d@gmail.com','ltd','123');


CREATE TABLE dia_chi (
    id UNIQUEIDENTIFIER DEFAULT NEWID() PRIMARY KEY,
    id_khach_hang UNIQUEIDENTIFIER ,
    ma NVARCHAR(50) UNIQUE ,
    thanh_pho NVARCHAR(30),
    huyen NVARCHAR(20),
    xa NVARCHAR(20),
    dia_chi_cu_the NVARCHAR(50),
    FOREIGN KEY (id_khach_hang) REFERENCES khach_hang(id)
);

CREATE TABLE phieu_giam_gia (
    id UNIQUEIDENTIFIER DEFAULT NEWID() PRIMARY KEY,
    ma NVARCHAR(50),
    ten NVARCHAR(200), 
    ngay_bat_dau DATE,
    ngay_ket_thuc DATE,
    hinh_thuc_giam_gia BIT,
    gia_tri_giam DEC,
    gia_tri_giam_toi_thieu DEC,
    gia_tri_giam_toi_da DEC,
    trang_thai BIT, --0 la ngung hoat dong, 1 la dang hoat dong
    mo_ta NVARCHAR(100)
);

CREATE TABLE phieu_giam_gia_ca_nhan (
    id UNIQUEIDENTIFIER DEFAULT NEWID() PRIMARY KEY,
    id_phieu_giam_gia UNIQUEIDENTIFIER , 
    id_khach_hang UNIQUEIDENTIFIER ,
    ma NVARCHAR(50),
    ten NVARCHAR(200),
    ngay_nhan DATE,
    ngay_het_han DATE,
    trang_thai BIT, --0 la ngung hoat dong, 1 la dang hoat dong
    FOREIGN KEY (id_phieu_giam_gia) REFERENCES phieu_giam_gia(id),
    FOREIGN KEY (id_khach_hang) REFERENCES khach_hang(id)
);

CREATE TABLE san_pham (
    id UNIQUEIDENTIFIER DEFAULT NEWID() PRIMARY KEY,
    id_hinh_anh UNIQUEIDENTIFIER ,
    id_thuong_hieu UNIQUEIDENTIFIER ,
    id_danh_muc UNIQUEIDENTIFIER ,
    id_xuat_xu UNIQUEIDENTIFIER ,
    id_de_giay UNIQUEIDENTIFIER ,
    id_day_giay UNIQUEIDENTIFIER ,
    id_muc_dich_su_dung UNIQUEIDENTIFIER ,
    ma NVARCHAR(50) UNIQUE ,
    ten NVARCHAR(300) ,
    ngay_tao DATE,
    ngay_cap_nhat DATE,
    nguoi_tao NVARCHAR(50),
    nguoi_cap_nhat NVARCHAR(50),
    trang_thai BIT,
    FOREIGN KEY (id_hinh_anh) REFERENCES hinh_anh(id),
    FOREIGN KEY (id_thuong_hieu) REFERENCES thuong_hieu(id),
    FOREIGN KEY (id_danh_muc) REFERENCES danh_muc(id),
    FOREIGN KEY (id_xuat_xu) REFERENCES xuat_xu(id),
    FOREIGN KEY (id_de_giay) REFERENCES de_giay(id),
    FOREIGN KEY (id_day_giay) REFERENCES day_giay(id),
    FOREIGN KEY (id_muc_dich_su_dung) REFERENCES muc_dich_su_dung(id)
);

CREATE TABLE chi_tiet_san_pham (
    id UNIQUEIDENTIFIER DEFAULT NEWID() PRIMARY KEY,
    id_san_pham UNIQUEIDENTIFIER ,
    id_mau_sac UNIQUEIDENTIFIER,
    id_kich_thuoc UNIQUEIDENTIFIER,
    id_chat_lieu UNIQUEIDENTIFIER,
    id_dot_giam_gia UNIQUEIDENTIFIER,
    ma NVARCHAR(50),
    gia_nhap DEC,
    gia_ban DEC,
    so_luong_ton INT,
    ngay_tao DATE,
    mo_ta NVARCHAR(100),
    FOREIGN KEY (id_san_pham) REFERENCES san_pham(id),
    FOREIGN KEY (id_mau_sac) REFERENCES mau_sac(id),
    FOREIGN KEY (id_kich_thuoc) REFERENCES kich_thuoc(id),
    FOREIGN KEY (id_chat_lieu) REFERENCES chat_lieu(id),
    FOREIGN KEY (id_dot_giam_gia) REFERENCES dot_giam_gia(id)
);

CREATE TABLE hoa_don (
    id UNIQUEIDENTIFIER DEFAULT NEWID() PRIMARY KEY,
    id_nhan_vien UNIQUEIDENTIFIER ,
    id_khach_hang UNIQUEIDENTIFIER,
    id_phieu_giam_gia UNIQUEIDENTIFIER,
    ma NVARCHAR(50),
    loai_hoa_don NVARCHAR(50),
    ten_san_pham NVARCHAR(100),
    phi_van_chuyen DEC,
    ten_khach_hang NVARCHAR(100),
    dia_chi_giao_hang NVARCHAR(100),
    sdt NVARCHAR(20),
    email_khach_hang VARCHAR(50),
    tong_tien DEC,
    tong_tien_sau_giam DEC,
    ngay_tao DATE,
    ngay_sua DATE,
    nguoi_tao NVARCHAR(50),
    nguoi_sua NVARCHAR(50),
    trang_thai TINYINT,
    ghi_chu NVARCHAR(100),
    FOREIGN KEY (id_nhan_vien) REFERENCES nhan_vien(id),
    FOREIGN KEY (id_khach_hang) REFERENCES khach_hang(id),
    FOREIGN KEY (id_phieu_giam_gia) REFERENCES phieu_giam_gia(id)
);

CREATE TABLE hoa_don_chi_tiet (
    id UNIQUEIDENTIFIER DEFAULT NEWID() PRIMARY KEY,
    id_chi_tiet_san_pham UNIQUEIDENTIFIER,
    id_hoa_don UNIQUEIDENTIFIER,
    ma NVARCHAR(50),
    so_luong INT,
    don_gia DEC,
    thanh_tien DEC,
    trang_thai TINYINT,
    FOREIGN KEY (id_chi_tiet_san_pham) REFERENCES chi_tiet_san_pham(id),
    FOREIGN KEY (id_hoa_don) REFERENCES hoa_don(id)
);

CREATE TABLE hinh_thuc_thanh_toan (
    id UNIQUEIDENTIFIER DEFAULT NEWID() PRIMARY KEY,
    id_hoa_don UNIQUEIDENTIFIER,
    id_phuong_thuc_thanh_toan UNIQUEIDENTIFIER,
    ma NVARCHAR(50),
    so_tien DEC,
    ghi_chu NVARCHAR(200),
    FOREIGN KEY (id_hoa_don) REFERENCES hoa_don(id),
    FOREIGN KEY (id_phuong_thuc_thanh_toan) REFERENCES phuong_thuc_thanh_toan(id)
);

CREATE TABLE lich_su_thanh_toan (
    id UNIQUEIDENTIFIER DEFAULT NEWID() PRIMARY KEY,
    id_hoa_don UNIQUEIDENTIFIER,
    id_hinh_thuc_thanh_toan UNIQUEIDENTIFIER,
    ma NVARCHAR(50),
    ngay_thanh_toan DATE,
    trang_thai TINYINT,
    FOREIGN KEY (id_hoa_don) REFERENCES hoa_don(id),
    FOREIGN KEY (id_hinh_thuc_thanh_toan) REFERENCES hinh_thuc_thanh_toan(id)
);

CREATE TABLE lich_su_hoa_don (
    id UNIQUEIDENTIFIER DEFAULT NEWID() PRIMARY KEY,
    id_hoa_don UNIQUEIDENTIFIER,
    thoi_gian DATETIME,
    nguoi_thuc_hien NVARCHAR(100),
    ghi_chu NVARCHAR(100),
    FOREIGN KEY (id_hoa_don) REFERENCES hoa_don(id)
);

-----------------------------------------------------------------------------------------------------------------------------------------
-- Mục đích sử dụng
INSERT INTO muc_dich_su_dung (ma, ten) VALUES
('MD01', N'Thể thao'), ('MD02', N'Di học'), ('MD03', N'Di làm');

-- Dây giày
INSERT INTO day_giay (ma, ten) VALUES
('DG01', N'Dây vải'), ('DG02', N'Dây cao su');

-- Đế giày
INSERT INTO de_giay (ma, ten) VALUES
('DE01', N'Đế cao su'), ('DE02', N'Đế PU');

-- Thương hiệu
INSERT INTO thuong_hieu (ma, ten) VALUES
('TH01', N'Nike'), ('TH02', N'Adidas'), ('TH03', N'Bitis');

-- Danh mục
INSERT INTO danh_muc (ma, ten) VALUES
('DM01', N'Giày thể thao'), ('DM02', N'Giày công sở');

-- Xuất xứ
INSERT INTO xuat_xu (ma, ten) VALUES
('XX01', N'Việt Nam'), ('XX02', N'Trung Quốc'), ('XX03', N'Mỹ');

-- Màu sắc
INSERT INTO mau_sac (ma, ten) VALUES
('MS01', N'Đen'), ('MS02', N'Trắng'), ('MS03', N'Đỏ');

-- Kích thước
INSERT INTO kich_thuoc (ma, ten) VALUES
('KT01', N'38'), ('KT02', N'39'), ('KT03', N'40');

-- Chất liệu
INSERT INTO chat_lieu (ma, ten) VALUES
('CL01', N'Da'), ('CL02', N'Vải'), ('CL03', N'Nhựa');

-- Hình ảnh
INSERT INTO hinh_anh (ma, url_anh1, url_anh2, url_anh3) VALUES
('HA01', 'url1_1.jpg', 'url1_2.jpg', 'url1_3.jpg'),
('HA02', 'url2_1.jpg', 'url2_2.jpg', 'url2_3.jpg');

-- Đợt giảm giá
INSERT INTO dot_giam_gia (ma, ten, gia_tri_giam, so_tien_giam_toi_da, ngay_bat_dau, ngay_ket_thuc, trang_thai) VALUES
('DG01', N'Mừng khai trương', 10, 50000, '2025-10-01', '2025-10-31', 1),
('DG02', N'Tết 2026', 15, 100000, '2025-12-01', '2026-02-01', 0);

-- Sản phẩm
INSERT INTO san_pham (
    id_hinh_anh, id_thuong_hieu, id_danh_muc, id_xuat_xu, id_de_giay, id_day_giay, id_muc_dich_su_dung,
    ma, ten, ngay_tao, ngay_cap_nhat, nguoi_tao, nguoi_cap_nhat, trang_thai
)
SELECT 
    ha.id, th.id, dm.id, xx.id, de.id, dg.id, md.id,
    'SP01', N'Giày Nike Air Max', GETDATE(), GETDATE(), 'admin', 'admin', 1
FROM 
    hinh_anh ha, thuong_hieu th, danh_muc dm, xuat_xu xx, de_giay de, day_giay dg, muc_dich_su_dung md
WHERE 
    ha.ma = 'HA01' AND th.ma = 'TH01' AND dm.ma = 'DM01' AND xx.ma = 'XX01'
    AND de.ma = 'DE01' AND dg.ma = 'DG01' AND md.ma = 'MD01';

-- Chi tiết sản phẩm
INSERT INTO chi_tiet_san_pham (
    id_san_pham, id_mau_sac, id_kich_thuoc, id_chat_lieu, id_dot_giam_gia,
    ma, gia_nhap, gia_ban, so_luong_ton, ngay_tao, mo_ta
)
SELECT 
    sp.id, ms.id, kt.id, cl.id, dgg.id,
    'CTSP01', 700000, 1000000, 50, GETDATE(), N'Giày thể thao chất lượng cao'
FROM 
    san_pham sp, mau_sac ms, kich_thuoc kt, chat_lieu cl, dot_giam_gia dgg
WHERE 
    sp.ma = 'SP01' AND ms.ma = 'MS01' AND kt.ma = 'KT01' AND cl.ma = 'CL01' AND dgg.ma = 'DG01';

    -- Địa chỉ khách hàng
INSERT INTO dia_chi (id_khach_hang, ma, thanh_pho, huyen, xa, dia_chi_cu_the)
SELECT id, 'DC01', N'Hà Nội', N'Ba Đình', N'Phúc Xá', N'Số 1 Phố Huế'
FROM khach_hang WHERE ma = 'KH01';

-- Phiếu giảm giá
INSERT INTO phieu_giam_gia (ma, ten, ngay_bat_dau, ngay_ket_thuc, hinh_thuc_giam_gia, gia_tri_giam, gia_tri_giam_toi_thieu, gia_tri_giam_toi_da, trang_thai, mo_ta)
VALUES ('PGG005', N'Giảm sinh nhật', '2025-10-01', '2025-10-20', 1, 10, 100000, 50000, 1, N'Áp dụng cho KH sinh nhật'),
('PGG001', N'Giảm 10% cho đơn hàng trên 500k', '2025-10-01', '2025-12-31', 1, 0.10, 500000, 200000, 1, N'Áp dụng cho toàn bộ sản phẩm'),
('PGG002', N'Giảm 50k cho đơn hàng từ 300k', '2025-11-01', '2025-11-30', 0, 50000, 300000, 50000, 1, N'Giảm giá trực tiếp'),
('PGG003', N'Giảm 20% cho sản phẩm mới', '2025-09-15', '2025-10-31', 1, 0.20, 100000, 150000, 0, N'Dành cho sản phẩm mới ra mắt'),
('PGG004', N'Voucher Black Friday', '2025-11-25', '2025-11-30', 1, 0.30, 1000000, 500000, 1, N'Áp dụng trong tuần Black Friday');

-- Giả sử có khách hàng:
-- KH01: 11111111-1111-1111-1111-111111111111
-- KH02: 22222222-2222-2222-2222-222222222222

-- Lấy id của phiếu giảm giá
DECLARE @idPGG1 UNIQUEIDENTIFIER = (SELECT TOP 1 id FROM phieu_giam_gia WHERE ma = 'PGG001');
DECLARE @idPGG2 UNIQUEIDENTIFIER = (SELECT TOP 1 id FROM phieu_giam_gia WHERE ma = 'PGG002');

DECLARE @idKH1 UNIQUEIDENTIFIER = (SELECT TOP 1 id FROM khach_hang WHERE ma = 'KH01');
DECLARE @idKH2 UNIQUEIDENTIFIER = (SELECT TOP 1 id FROM khach_hang WHERE ma = 'KH02');

INSERT INTO phieu_giam_gia_ca_nhan (id_phieu_giam_gia, id_khach_hang, ma, ten, ngay_nhan, ngay_het_han, trang_thai)
VALUES
(@idPGG1, @idKH1, 'PGG_CN001', N'Voucher 10% cho Nguyễn Văn C', '2025-10-10', '2025-12-31', 1),
(@idPGG1, @idKH2, 'PGG_CN002', N'Voucher 10% cho Lê Thị D', '2025-10-15', '2025-12-31', 1),
(@idPGG2, @idKH1, 'PGG_CN003', N'Voucher 50k cá nhân', '2025-11-01', '2025-11-30', 1),
(@idPGG2, @idKH2, 'PGG_CN004', N'Voucher Black Friday cá nhân', '2025-11-25', '2025-11-30', 0);


-- Gán phiếu cho khách hàng
INSERT INTO phieu_giam_gia_ca_nhan (id_phieu_giam_gia, id_khach_hang, ma, ten, ngay_nhan, ngay_het_han, trang_thai)
SELECT pgg.id, kh.id, 'PGGKH01', N'Giảm giá cho Nguyễn Văn C', '2025-10-01', '2025-10-20', 1
FROM phieu_giam_gia pgg, khach_hang kh WHERE pgg.ma = 'PGG01' AND kh.ma = 'KH01';

-- Hóa đơn
INSERT INTO hoa_don (
    id_nhan_vien, id_khach_hang, id_phieu_giam_gia, ma, loai_hoa_don, ten_san_pham, phi_van_chuyen,
    ten_khach_hang, dia_chi_giao_hang, sdt, email_khach_hang, tong_tien, tong_tien_sau_giam,
    ngay_tao, ngay_sua, nguoi_tao, nguoi_sua, trang_thai, ghi_chu
)
SELECT 
    nv.id, kh.id, pgg.id, 'HD01', N'Online', N'Giày Nike Air Max', 30000,
    kh.ho_ten, N'Số 1 Phố Huế, Hà Nội', '0912345678', kh.email,
    1000000, 900000, GETDATE(), GETDATE(), 'admin', 'admin', 1, N'Đơn hàng online'
FROM nhan_vien nv, khach_hang kh, phieu_giam_gia pgg
WHERE nv.ma = 'NV01' AND kh.ma = 'KH01' AND pgg.ma = 'PGG01';

-- Chi tiết hóa đơn
INSERT INTO hoa_don_chi_tiet (
    id_chi_tiet_san_pham, id_hoa_don, ma, so_luong, don_gia, thanh_tien, trang_thai
)
SELECT ctsp.id, hd.id, 'HDCT01', 1, 1000000, 900000, 1
FROM chi_tiet_san_pham ctsp, hoa_don hd
WHERE ctsp.ma = 'CTSP01' AND hd.ma = 'HD01';

-- Hình thức thanh toán
INSERT INTO hinh_thuc_thanh_toan (id_hoa_don, id_phuong_thuc_thanh_toan, ma, so_tien, ghi_chu)
SELECT hd.id, pttt.id, 'HTTT01', 900000, N'Thanh toán chuyển khoản'
FROM hoa_don hd, phuong_thuc_thanh_toan pttt
WHERE hd.ma = 'HD01' AND pttt.ma = 'PT02';

-- Lịch sử thanh toán
INSERT INTO lich_su_thanh_toan (id_hoa_don, id_hinh_thuc_thanh_toan, ma, ngay_thanh_toan, trang_thai)
SELECT hd.id, httt.id, 'LSTT01', GETDATE(), 1
FROM hoa_don hd, hinh_thuc_thanh_toan httt
WHERE hd.ma = 'HD01' AND httt.ma = 'HTTT01';
