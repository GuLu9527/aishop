-- AI知识库初始化数据
INSERT INTO ai_knowledge_base (title, content, category, keywords, intent, priority, status, create_time, update_time) VALUES
('退货政策', '我们支持7天无理由退货，商品需保持完好包装。退货流程：1.联系客服申请退货 2.填写退货单 3.寄回商品 4.审核通过后退款', '售后服务', '["退货", "退款", "无理由退货", "7天"]', 'refund', 10, 1, NOW(), NOW()),

('订单查询', '您可以通过以下方式查询订单：1.登录账户在"我的订单"中查看 2.使用订单号查询 3.联系客服协助查询', '订单服务', '["订单", "查询", "订单状态", "物流"]', 'order', 9, 1, NOW(), NOW()),

('商品推荐', '我们有丰富的商品分类：1.生鲜食品 2.日用百货 3.家电数码 4.服装鞋帽。可根据您的需求为您推荐合适商品', '商品服务', '["商品", "推荐", "分类", "购买"]', 'product', 8, 1, NOW(), NOW()),

('优惠活动', '当前优惠活动：1.新用户注册送优惠券 2.满99减10元 3.会员专享折扣 4.每日特价商品。详情请查看活动页面', '营销活动', '["优惠", "活动", "折扣", "优惠券", "特价"]', 'promotion', 8, 1, NOW(), NOW()),

('配送服务', '配送说明：1.市内当日达 2.满59元免运费 3.支持上门配送 4.可选择配送时间段。特殊商品可能有配送限制', '配送服务', '["配送", "送货", "运费", "物流", "快递"]', 'delivery', 7, 1, NOW(), NOW()),

('支付方式', '支持多种支付方式：1.微信支付 2.支付宝 3.银行卡支付 4.现金支付(线下) 5.货到付款。支付安全有保障', '支付服务', '["支付", "付款", "微信", "支付宝", "银行卡"]', 'payment', 7, 1, NOW(), NOW()),

('会员服务', '会员权益：1.积分奖励 2.专享折扣 3.生日特权 4.优先客服 5.免费配送。注册即可成为会员', '会员服务', '["会员", "积分", "权益", "注册", "等级"]', 'membership', 6, 1, NOW(), NOW()),

('联系客服', '客服联系方式：1.在线客服(工作时间9:00-21:00) 2.客服热线400-xxx-xxxx 3.微信客服 4.邮箱service@supermarket.com', '客服服务', '["客服", "联系", "电话", "在线", "人工"]', 'contact', 9, 1, NOW(), NOW());