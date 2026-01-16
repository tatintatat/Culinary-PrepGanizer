-- phpMyAdmin SQL Dump
-- version 5.2.3
-- https://www.phpmyadmin.net/
--
-- Host: 127.0.0.1:3306
-- Generation Time: Dec 20, 2025 at 01:20 PM
-- Server version: 8.4.7
-- PHP Version: 8.3.28

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Database: `tbl_username_password`
--

-- --------------------------------------------------------

--
-- Table structure for table `tbl_login_history`
--

DROP TABLE IF EXISTS `tbl_login_history`;
CREATE TABLE IF NOT EXISTS `tbl_login_history` (
  `login_id` int NOT NULL AUTO_INCREMENT,
  `username` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `login_time` datetime DEFAULT CURRENT_TIMESTAMP,
  `login_status` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `ip_address` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `device_info` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  PRIMARY KEY (`login_id`)
) ENGINE=MyISAM AUTO_INCREMENT=55 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

--
-- Dumping data for table `tbl_login_history`
--

INSERT INTO `tbl_login_history` (`login_id`, `username`, `login_time`, `login_status`, `ip_address`, `device_info`) VALUES
(30, 'marcivancastro@gmail.com', '2025-11-29 11:23:34', 'SUCCESS', NULL, 'Windows 11 10.0'),
(34, 'marcivancastro@gmial.com', '2025-11-29 12:34:12', 'FAILED', NULL, 'Windows 11 10.0'),
(35, 'marcivancastro@gmail.com', '2025-11-29 12:34:26', 'SUCCESS', NULL, 'Windows 11 10.0'),
(33, 'marcivancastro@gmail.com', '2025-11-29 12:23:46', 'SUCCESS', NULL, 'Windows 11 10.0'),
(32, 'marcivancastro@gmail.com', '2025-11-29 12:21:12', 'SUCCESS', NULL, 'Windows 11 10.0'),
(31, 'marcivancastro@gmail.com', '2025-11-29 12:19:31', 'SUCCESS', NULL, 'Windows 11 10.0'),
(36, 'marcivancastro@gmail.com', '2025-12-02 10:30:19', 'SUCCESS', NULL, 'Windows 11 10.0'),
(37, 'edmiguelangeles@gmail.com', '2025-12-02 10:33:10', 'SUCCESS', NULL, 'Windows 11 10.0'),
(38, '0320-2797@lspu.edu.ph', '2025-12-02 15:06:37', 'SUCCESS', NULL, 'Windows 11 10.0'),
(39, 'marcivancastro@gmail.com', '2025-12-02 15:19:13', 'SUCCESS', NULL, 'Windows 11 10.0'),
(40, 'justineerickisleta5@gmail.com', '2025-12-02 23:58:16', 'SUCCESS', NULL, 'Windows 11 10.0'),
(41, 'marcivancastro@gmail.com', '2025-12-03 11:01:20', 'SUCCESS', NULL, 'Windows 11 10.0'),
(42, 'marcivancastro@gmail.com', '2025-12-03 11:01:56', 'SUCCESS', NULL, 'Windows 11 10.0'),
(43, 'marcivancastro@gmail.com', '2025-12-03 11:33:44', 'SUCCESS', NULL, 'Windows 11 10.0'),
(44, 'marcivancastro@gmail.com', '2025-12-03 11:38:36', 'SUCCESS', NULL, 'Windows 11 10.0'),
(45, 'marcivancastro@gmail.com', '2025-12-03 11:55:57', 'SUCCESS', NULL, 'Windows 11 10.0'),
(46, 'justineerickisleta5@gmail.com', '2025-12-04 21:23:48', 'SUCCESS', NULL, 'Windows 11 10.0'),
(47, 'justineerickisleta5@gmail.com', '2025-12-06 20:36:12', 'SUCCESS', NULL, 'Windows 11 10.0'),
(48, 'justineerickisleta5@gmail.com', '2025-12-06 20:40:58', 'SUCCESS', NULL, 'Windows 11 10.0'),
(49, 'justineerickisleta5@gmail.com', '2025-12-06 20:45:15', 'SUCCESS', NULL, 'Windows 11 10.0'),
(50, 'justineerickisleta5@gmail.com', '2025-12-06 20:56:00', 'SUCCESS', NULL, 'Windows 11 10.0'),
(51, 'justineerickisleta5@gmail.com', '2025-12-06 21:05:29', 'SUCCESS', NULL, 'Windows 11 10.0'),
(52, 'justineerickisleta5@gmail.com', '2025-12-06 21:07:38', 'SUCCESS', NULL, 'Windows 11 10.0'),
(53, 'marcivancastro@gmail.com', '2025-12-07 12:50:43', 'SUCCESS', NULL, 'Windows 11 10.0'),
(54, 'justineerickisleta5@gmail.com', '2025-12-07 13:10:40', 'SUCCESS', NULL, 'Windows 11 10.0');

-- --------------------------------------------------------

--
-- Table structure for table `tbl_registration`
--

DROP TABLE IF EXISTS `tbl_registration`;
CREATE TABLE IF NOT EXISTS `tbl_registration` (
  `user_Id` int NOT NULL AUTO_INCREMENT,
  `username` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `password` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `last_login` datetime DEFAULT NULL,
  `registration_date` datetime DEFAULT NULL,
  PRIMARY KEY (`user_Id`)
) ENGINE=MyISAM AUTO_INCREMENT=3286 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

--
-- Dumping data for table `tbl_registration`
--

INSERT INTO `tbl_registration` (`user_Id`, `username`, `password`, `last_login`, `registration_date`) VALUES
(3265, 'marcivancastro@gmail.com', 'x9wZJV2nUWbMdIGLlZOM4vpKTvIFADXIBsuoFI+fA5w=', '2025-12-07 12:50:43', '2025-11-29 11:13:33'),
(3259, 'castromarcivan9@gmail.com', 'x9wZJV2nUWbMdIGLlZOM4vpKTvIFADXIBsuoFI+fA5w=', NULL, '2025-11-25 16:39:10'),
(3260, '0324-0500@lspu.edu.ph', 'x9wZJV2nUWbMdIGLlZOM4vpKTvIFADXIBsuoFI+fA5w=', NULL, '2025-11-25 16:42:26'),
(3284, 'benedictflores12101@gmail.com', 'do8vZS5i4x+oI3zIa46UNgl+tk2mGtIw9XiTTAHE/j4=', NULL, '2025-12-19 17:33:46'),
(3266, 'edmiguelangeles@gmail.com', 'n2f3DhpyXBGaWQflZHuMlkAEVjtZEKWtxNHlkQHFcXc=', '2025-12-02 10:33:10', '2025-12-02 10:32:22'),
(3285, 'justineerickisleta5@gmail.com', 'ji97QhwU5c51LaezyL4mJWI0sgJRciVWiC5HEKscZTc=', NULL, '2025-12-19 17:45:28'),
(3270, 'ivancastro@gmail.com', 'lI66nLyUS9kW2GNfD1wVkYzwDls0Uht/qzjrtlexae0=', NULL, '2025-12-07 13:19:37'),
(3273, 'itnotryo@gmail.com', 'o67S5MIL7Pjyjz4nCfsZuhFawOfNt5JRaG7NeJ1zfDs=', NULL, '2025-12-08 12:36:50'),
(3272, 'naitharo@gmail.com', 'D9kByYc+R+yjnBkzxDoNVo3q5sgPJXwij4Wap5O8dTc=', NULL, '2025-12-08 12:35:13'),
(3274, 'francisluisreyes001@gmail.com', 'trAFObbbEqC5jxPYEdGo3d0weR9h3idkgHYba9cki98=', NULL, '2025-12-08 12:38:29'),
(3277, 'suminomajo09@gmail.com', 'Ky5F4QMxnGRcP1W6xH2mvP43vR//etdEaylv+fIzI1E=', NULL, '2025-12-10 13:18:18'),
(3278, 'dexterbenitez31@gmail.com', 'c4M1D03I6QfQ+Zm2DdgJp1GMBOO4uPt3MjfRX0m3B3o=', NULL, '2025-12-12 15:30:24'),
(3279, 'jhadezymond05@gmail.com', 'AY5na01/Ghj8TVCEM4xDnHkZWujNezOaMigDWqyndyM=', NULL, '2025-12-18 13:38:54');
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
