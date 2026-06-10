CREATE TABLE IF NOT EXISTS design_style (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    category VARCHAR(50) NOT NULL,
    description TEXT NOT NULL,
    features TEXT,
    color_palette VARCHAR(500),
    suitable_spaces VARCHAR(500),
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS design_chunk (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    style_id BIGINT,
    chunk_index INT NOT NULL,
    content TEXT NOT NULL,
    embedding BLOB,
    token_count INT DEFAULT 0,
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Seed data: design style knowledge base
INSERT INTO design_style (name, category, description, features, color_palette, suitable_spaces) VALUES
('现代简约', 'modern', '以少胜多的设计哲学，强调功能至上、线条简洁、空间通透', '无主灯设计、隐藏式收纳、几何造型、中性色调', '#FFFFFF,#F5F5F5,#333333,#9E9E9E', '客厅、卧室、书房'),
('北欧风', 'modern', '源自斯堪的纳维亚，注重自然采光、温暖材质与功能实用', '浅木色地板、大面积白色、绿植点缀、羊毛地毯', '#FAFAFA,#E0D8C8,#A8C5A0,#4A4A4A', '客厅、卧室、餐厅'),
('新中式', 'eastern', '传统中式元素与现代设计语言的融合，讲究对称与意境', '实木家具、水墨装饰、屏风隔断、陶瓷摆件', '#8B4513,#D4C5B9,#2C1810,#C41E3A', '客厅、书房、茶室'),
('工业风', 'contemporary', '粗犷不羁的Loft风格，暴露建筑结构，金属与木质混搭', '裸露砖墙、金属灯具、水泥地面、铁艺家具', '#808080,#8B4513,#2C2C2C,#D2691E', '工作室、loft、公共空间'),
('日式侘寂', 'eastern', '接受不完美的美学，追求自然质朴、岁月痕迹与宁静感', '榻榻米、障子门、原木家具、手工陶器', '#D4C5B9,#8B8682,#6B8E7B,#F5F0E8', '茶室、卧室、禅修空间'),
('轻奢风', 'contemporary', '低调的奢华感，大理石、金属、丝绒等材质精妙搭配', '大理石台面、黄铜灯具、丝绒沙发、镜面装饰', '#D4AF37,#2C2C2C,#F5F5F0,#800020', '客厅、餐厅、主卧'),
('地中海风', 'regional', '源自地中海沿岸的浪漫风格，蓝白主调与自然材质', '拱门造型、马赛克瓷砖、白墙、蓝窗', '#FFFFFF,#1E90FF,#87CEEB,#D2691E', '厨房、浴室、阳台'),
('美式乡村', 'regional', '温馨怀旧的田园风格，强调舒适实用与自然质感', '壁炉、布艺沙发、实木地板、花卉图案', '#8B4513,#F5DEB3,#556B2F,#CD853F', '客厅、厨房、卧室'),
('极简主义', 'modern', '极致的简约，去除一切非必要装饰，强调空间本质', '纯白色调、隐藏灯光、无缝地面、极少量家具', '#FFFFFF,#F8F8F8,#E8E8E8,#000000', '工作室、卧室、展示空间'),
('Art Deco', 'contemporary', '1920年代兴起的装饰艺术风格，几何图案与奢华材质', '放射状线条、金色装饰、黑色与金色对比、镜面', '#000000,#FFD700,#C0C0C0,#800020', '酒店大堂、会所、客厅'),
('波西米亚风', 'regional', '自由不羁的混搭风格，丰富的色彩层次与异域元素', '民族图案、藤编家具、吊椅、大量绿植', '#8B0000,#DAA520,#556B2F,#6A5ACD,#FF6347', '客厅、卧室、阳台'),
('奶油风', 'modern', '2020年代流行的温柔风格，低饱和度暖色调与柔和弧线', '弧形墙面、奶油色调、藤编元素、柔软布艺', '#FFFDD0,#F5DEB3,#D2B48C,#FFF8DC', '卧室、客厅、儿童房'),
('原木风', 'eastern', '以天然木材为核心的自然风格，强调纹理与温暖触感', '原木家具、木格栅、藤编灯饰、棉麻布艺', '#DEB887,#8B4513,#F5DEB3,#556B2F', '全屋通用'),
('暗黑风', 'contemporary', '深色调主导的高级感空间，戏剧性的光影对比', '深色墙面、重点照明、玻璃隔断、金属点缀', '#1A1A1A,#404040,#808080,#D4AF37', '影音室、酒吧、个性化空间'),
('法式复古', 'regional', '优雅浪漫的法式情调，石膏线条与复古家具搭配', '石膏线、壁炉、人字拼地板、水晶吊灯', '#FFFFFF,#E8D5B7,#C4A882,#87CEEB', '客厅、卧室、餐厅');
