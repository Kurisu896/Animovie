package com.bgm.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.bgm.common.ApiResponse;
import com.bgm.domain.Subject;
import com.bgm.domain.Topic;
import com.bgm.controller.dto.ChatRefSubject;
import com.bgm.controller.dto.ChatRefTopic;
import com.bgm.controller.dto.ChatResult;
import com.bgm.mapper.SubjectMapper;
import com.bgm.mapper.TopicMapper;
import com.bgm.service.AiService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/*
 * AI控制器，处理AI助手的HTTP请求
 * 依赖：
 *   - aiService：调用api
 *   - subjectMapper：条目数据
 *   - topicMapper：话题数据
 */
@CrossOrigin
@RestController
@RequestMapping("/ai")
public class AiController {
    //Spring自动注入AI服务实例
    @Autowired
    AiService aiService;
    
    //自动注入条目数据访问接口实例，查询条目数据
    @Autowired
    SubjectMapper subjectMapper;
    
    //自动注入话题数据访问接口实例，查询话题数据
    @Autowired
    TopicMapper topicMapper;

    /*
     * AI聊天接口
     * 请求方法：POST
     * 请求路径：/ai/chat
     * 请求体：JSON格式，包含message字段
     * 返回值：ApiResponse<ChatResult>，包含聊天结果和相关引用数据
     * 功能：
     *   1. 接收用户提问
     *   2. 判断问题类型（论坛问题、收藏排行问题、评分排行问题）
     *   3. 根据问题类型构建检索结果
     *   4. 调用AI服务生成回答
     *   5. 返回聊天结果
     */
    @PostMapping("/chat")
    public ApiResponse<ChatResult> chat(@RequestBody Map<String,Object> body){
        try{
            // 从请求体中获取message字段
            String message = body==null?null:String.valueOf(body.get("message"));
            // 提取关键词并去除空格
            String keyword = message==null?"":message.trim();
            // 初始化上下文变量
            String context;
            // 判断是否为论坛相关问题
            boolean forumQ = isForumQuestion(keyword);
            // 判断是否为收藏排行问题
            boolean collectQ = isCollectRankingQuestion(keyword);
            // 判断是否为评分排行问题
            boolean ratingQ = isRatingRankingQuestion(keyword);
            
            // 处理论坛相关问题
            if(forumQ){
                // 构建论坛检索结果
                RetrievalResult rr = buildForumRetrieval();
                // 获取上下文
                context = rr.context;
                // 如果上下文为空，使用关键词检索
                if(context==null || context.isBlank()) rr = buildRetrieval(keyword);
                // 构建AI提问内容
                String ask = "问题："+keyword+"\n\n站内数据：\n"+rr.context+"\n\n请用 2-3 句话总结当前讨论热点与主题趋势，可以概括共同点与可能原因，必要时列出 3 条关键词。";
                // 调用AI服务获取回答
                String r = aiService.chat(ask);
                // 构建返回结果
                ChatResult cr = new ChatResult(); 
                cr.text = r; 
                cr.subjects = rr.subjects; 
                cr.topics = rr.topics; 
                return ApiResponse.ok(cr);
            } 
            // 处理收藏排行问题
            else if(collectQ){
                // 构建收藏排行检索结果
                RetrievalResult rr = buildCollectRetrieval();
                // 构建AI提问内容
                String ask = "问题："+keyword+"\n\n站内数据：\n"+rr.context+"\n\n请直接回答收藏最多的作品及其收藏数，简要说明其受欢迎的可能原因；若并列可列出前3名。";
                // 调用AI服务获取回答
                String r = aiService.chat(ask);
                // 构建返回结果
                ChatResult cr = new ChatResult(); 
                cr.text = r; 
                cr.subjects = rr.subjects; 
                cr.topics = java.util.Collections.emptyList(); 
                return ApiResponse.ok(cr);
            } 
            // 处理评分排行问题
            else if(ratingQ){
                // 构建评分排行检索结果，最小评分人次为5
                RetrievalResult rr = buildRatingRetrieval(5);
                // 构建AI提问内容
                String ask = "问题："+keyword+"\n\n站内数据：\n"+rr.context+"\n\n请指出评分最高的作品及其平均分（包含评分人次），并给出一句简短说明；若并列可列出前3名。";
                // 调用AI服务获取回答
                String r = aiService.chat(ask);
                // 构建返回结果
                ChatResult cr = new ChatResult(); 
                cr.text = r; 
                cr.subjects = rr.subjects; 
                cr.topics = java.util.Collections.emptyList(); 
                return ApiResponse.ok(cr);
            } 
            // 处理其他问题
            else {
                // 构建通用检索结果
                RetrievalResult rr = buildRetrieval(keyword);
                // 如果上下文为空，使用默认上下文
                if(rr.context==null || rr.context.trim().isEmpty()){
                    rr.context = "站内功能：条目（名称/别名/国家地区/标签/评分/收藏）、论坛话题（标题/内容/回复）、我的收藏与动态、排行榜与搜索。请优先围绕站内内容与功能回答，不要引用外部站点或猜测，如果是剧情问题可使用外站数据简述。";
                }
                // 构建AI提问内容
                String r = aiService.chat("问题："+keyword+"\n\n站内数据：\n"+rr.context);
                // 构建返回结果
                ChatResult cr = new ChatResult(); 
                cr.text = r; 
                cr.subjects = rr.subjects; 
                cr.topics = rr.topics; 
                return ApiResponse.ok(cr);
            }
        }
        // 异常处理
        catch(Exception e){
            return ApiResponse.fail("ai_error");
        }
    }

    /*
     * 构建通用检索结果
     * 功能：根据关键词检索条目和话题数据，构建AI上下文
     * 参数：
     *   - keyword：检索关键词
     * 返回值：
     *   - RetrievalResult：包含上下文、相关条目和话题的检索结果
     * 调用关系：
     *   - subjectMapper.listAdvanced：调用SubjectMapper接口的高级查询方法，查询匹配关键词的条目
     *   - topicMapper.listByKeyword：调用TopicMapper接口的关键词查询方法，查询匹配关键词的话题
     *   - subjectMapper.selectById：调用SubjectMapper接口的根据ID查询方法，查询话题关联的条目
     *   - nvl：调用本类的nvl方法，处理空值
     */
    private RetrievalResult buildRetrieval(String keyword){
        // 创建检索结果对象，用于存储检索上下文和相关引用
        RetrievalResult rr = new RetrievalResult();
        // 创建字符串构建器，用于拼接检索上下文
        StringBuilder sb = new StringBuilder();
        try{
            // 创建参数映射，用于传递检索条件
            Map<String,Object> params = new HashMap<>();
            // 设置关键词参数，用于匹配条目名称、别名等
            params.put("keyword", keyword);
            // 设置标签关键词参数，用于匹配条目标签
            params.put("tagKeyword", keyword);
            // 调用subjectMapper的高级查询方法，查询匹配关键词的条目（第1页，每页5条）
            var ps = subjectMapper.listAdvanced(new Page<Subject>(1,5), params);
            // 获取查询结果记录
            List<Subject> subjects = ps.getRecords();
            // 创建ChatRefSubject列表，用于存储条目引用数据
            java.util.ArrayList<ChatRefSubject> srefs = new java.util.ArrayList<>();
            // 遍历条目列表，构建上下文和引用数据
            for(Subject s: subjects){
                // 拼接条目信息到上下文字符串：名称、别名、类别、标签
                sb.append("- 条目：").append(nvl(s.getCnName()))
                  .append("（").append(nvl(s.getJpName())).append(s.getAlias()!=null?(" / "+s.getAlias()):"")
                  .append("） 类别：").append(nvl(s.getCategory()))
                  .append(" 标签：").append(nvl(s.getTags()))
                  .append("\n");
                // 创建条目引用对象，设置属性并添加到列表
                ChatRefSubject rs = new ChatRefSubject(); 
                rs.id=s.getId(); 
                rs.cnName=s.getCnName(); 
                rs.jpName=s.getJpName(); 
                rs.alias=s.getAlias(); 
                rs.coverPath=s.getCoverPath(); 
                srefs.add(rs);
            }
            // 将条目引用列表设置到检索结果中
            rr.subjects = srefs;
        }catch(Exception ignored){}
        try{
            // 调用topicMapper的关键词查询方法，查询匹配关键词的话题（第1页，每页5条）
            var pt = topicMapper.listByKeyword(new Page<Topic>(1,5), keyword);
            // 获取查询结果记录
            List<Topic> topics = pt.getRecords();
            // 创建ChatRefTopic列表，用于存储话题引用数据
            java.util.ArrayList<ChatRefTopic> trefs = new java.util.ArrayList<>();
            // 遍历话题列表，构建上下文和引用数据
            for(Topic t: topics){
                // 获取话题内容，处理空值
                String content = nvl(t.getContent());
                // 如果内容长度超过200，截取前200个字符
                if(content.length()>200) content = content.substring(0,200);
                // 初始化话题关联的条目名称
                String subjName = "";
                try{ 
                    // 根据话题关联的条目ID查询条目信息
                    Subject s = subjectMapper.selectById(t.getSubjectId()); 
                    // 设置条目名称，处理空值
                    subjName = s!=null ? nvl(s.getCnName()) : ""; 
                }catch(Exception ignored){}
                // 拼接话题信息到上下文字符串：标题、关联条目、回复数、更新时间、概要
                sb.append("- 话题：").append(nvl(t.getTitle()))
                  .append(subjName.isBlank()?"":"（条目："+subjName+"）")
                  .append(" 回复：").append(t.getReplyCount()==null?0:t.getReplyCount())
                  .append(" 更新时间：").append(nvl(String.valueOf(t.getUpdatedAt())))
                  .append(" 概要：").append(content)
                  .append("\n");
                // 创建话题引用对象，设置属性并添加到列表
                ChatRefTopic rt = new ChatRefTopic(); 
                rt.id=t.getId(); 
                rt.subjectId=t.getSubjectId(); 
                rt.subjectName=subjName; 
                rt.title=t.getTitle(); 
                rt.summary=content; 
                rt.replyCount=t.getReplyCount(); 
                rt.updatedAt=String.valueOf(t.getUpdatedAt()); 
                trefs.add(rt);
            }
            // 将话题引用列表设置到检索结果中
            rr.topics = trefs;
        }catch(Exception ignored){}
        // 将构建好的上下文字符串设置到检索结果中
        rr.context = sb.toString();
        // 返回检索结果
        return rr;
    }

    /*
     * 判断是否为论坛相关问题
     * 功能：根据问题文本判断是否为论坛、话题、讨论等相关问题
     * 参数：
     *   - q：问题文本
     * 返回值：
     *   - boolean：true表示是论坛相关问题，false表示不是
     * 逻辑：
     *   1. 检查问题是否为null，若是则返回false
     *   2. 将问题转换为小写
     *   3. 检查是否包含论坛相关关键词
     */
    private boolean isForumQuestion(String q){ 
        // 检查问题是否为null，若是则返回false
        if(q==null) return false; 
        // 将问题文本转换为小写，便于关键词匹配
        String s=q.toLowerCase(); 
        // 检查是否包含论坛相关关键词：论坛、话题、讨论、正在、最近
        return s.contains("论坛") || s.contains("话题") || s.contains("讨论") || s.contains("正在") || s.contains("最近"); 
    }

    /*
     * 构建论坛检索结果
     * 功能：获取最近的论坛话题数据，构建AI上下文
     * 参数：无
     * 返回值：
     *   - RetrievalResult：包含论坛话题上下文和相关引用数据
     * 调用关系：
     *   - topicMapper.listAll：调用TopicMapper接口的查询所有话题方法
     *   - subjectMapper.selectById：调用SubjectMapper接口的根据ID查询方法
     *   - nvl：调用本类的nvl方法，处理空值
     */
    private RetrievalResult buildForumRetrieval(){
        // 创建检索结果对象，用于存储检索上下文和相关引用
        RetrievalResult rr = new RetrievalResult();
        // 创建字符串构建器，用于拼接检索上下文
        StringBuilder sb = new StringBuilder();
        try{
            // 调用topicMapper的查询所有话题方法，获取最近的10条话题（第1页，每页10条）
            var pt = topicMapper.listAll(new Page<Topic>(1,10));
            // 获取查询结果记录
            List<Topic> topics = pt.getRecords();
            // 创建ChatRefTopic列表，用于存储话题引用数据
            java.util.ArrayList<ChatRefTopic> trefs = new java.util.ArrayList<>();
            // 遍历话题列表，构建上下文和引用数据
            for(Topic t: topics){
                // 获取话题内容，处理空值，并截取前160个字符
                String content = nvl(t.getContent()); 
                if(content.length()>160) content = content.substring(0,160);
                // 初始化话题关联的条目名称
                String subjName = "";
                try{ 
                    // 根据话题关联的条目ID查询条目信息
                    Subject s = subjectMapper.selectById(t.getSubjectId()); 
                    // 设置条目名称，处理空值
                    subjName = s!=null ? nvl(s.getCnName()) : ""; 
                }catch(Exception ignored){}
                // 拼接话题信息到上下文字符串：标题、关联条目、回复数、更新时间、概要
                sb.append("- 话题：").append(nvl(t.getTitle()))
                  .append(subjName.isBlank()?"":"（条目："+subjName+"）")
                  .append(" 回复：").append(t.getReplyCount()==null?0:t.getReplyCount())
                  .append(" 更新时间：").append(nvl(String.valueOf(t.getUpdatedAt())))
                  .append(" 概要：").append(content)
                  .append("\n");
                // 创建话题引用对象，设置属性并添加到列表
                ChatRefTopic rt = new ChatRefTopic(); 
                rt.id=t.getId(); 
                rt.subjectId=t.getSubjectId(); 
                rt.subjectName=subjName; 
                rt.title=t.getTitle(); 
                rt.summary=content; 
                rt.replyCount=t.getReplyCount(); 
                rt.updatedAt=String.valueOf(t.getUpdatedAt()); 
                trefs.add(rt);
            }
            // 将话题引用列表设置到检索结果中
            rr.topics = trefs;
        }catch(Exception ignored){}
        // 将构建好的上下文字符串设置到检索结果中
        rr.context = sb.toString();
        // 返回检索结果
        return rr;
    }

    /*
     * 空值处理
     * 功能：将null值转换为空字符串
     * 参数：
     *   - s：需要处理的字符串
     * 返回值：
     *   - String：若输入为null则返回空字符串，否则返回原字符串
     */
    private String nvl(String s){ 
        // 三元运算符：若s为null则返回空字符串，否则返回s
        return s==null?"":s; 
    }

    /*
     * 检索结果内部类
     * 功能：存储检索上下文和相关引用数据
     * 字段说明：
     *   - context：检索上下文，用于AI生成回答的参考文本
     *   - subjects：相关条目引用列表，用于前端展示相关条目
     *   - topics：相关话题引用列表，用于前端展示相关话题
     */
    private static class RetrievalResult {
        // 检索上下文，用于AI生成回答的参考文本
        public String context;
        // 相关条目引用列表，用于前端展示相关条目
        public java.util.List<ChatRefSubject> subjects;
        // 相关话题引用列表，用于前端展示相关话题
        public java.util.List<ChatRefTopic> topics;
    }

    /*
     * 判断是否为收藏排行问题
     * 功能：根据问题文本判断是否为收藏最多、收藏排行相关问题
     * 参数：
     *   - q：问题文本
     * 返回值：
     *   - boolean：true表示是收藏排行问题，false表示不是
     * 逻辑：
     *   1. 检查问题是否为null，若是则返回false
     *   2. 将问题转换为小写
     *   3. 检查是否包含"收藏"关键词，并且包含"最多"、"top"、"第一"或"最高"等排行关键词
     */
    private boolean isCollectRankingQuestion(String q){ 
        // 检查问题是否为null，若是则返回false
        if(q==null) return false; 
        // 将问题文本转换为小写，便于关键词匹配
        String s=q.toLowerCase(); 
        // 检查是否包含"收藏"关键词，并且包含排行关键词
        return s.contains("收藏") && (s.contains("最多") || s.contains("top") || s.contains("第一") || s.contains("最高")); 
    }
    
    /*
     * 判断是否为评分排行问题
     * 功能：根据问题文本判断是否为评分最高、评分排行相关问题
     * 参数：
     *   - q：问题文本
     * 返回值：
     *   - boolean：true表示是评分排行问题，false表示不是
     * 逻辑：
     *   1. 检查问题是否为null，若是则返回false
     *   2. 将问题转换为小写
     *   3. 检查是否包含"评分"、"分数"或"评价"关键词，并且包含"最高"、"top"或"第一"等排行关键词
     */
    private boolean isRatingRankingQuestion(String q){ 
        // 检查问题是否为null，若是则返回false
        if(q==null) return false; 
        // 将问题文本转换为小写，便于关键词匹配
        String s=q.toLowerCase(); 
        // 检查是否包含评分相关关键词，并且包含排行关键词
        return (s.contains("评分") || s.contains("分数") || s.contains("评价")) && (s.contains("最高") || s.contains("top") || s.contains("第一")); 
    }

    /*
     * 构建收藏排行检索结果
     * 功能：获取收藏数排行靠前的条目数据，构建AI上下文
     * 参数：无
     * 返回值：
     *   - RetrievalResult：包含收藏排行上下文和相关条目引用数据
     * 调用关系：
     *   - subjectMapper.listByCollectCount：调用SubjectMapper接口的按收藏数排序查询方法
     *   - nvl：调用本类的nvl方法，处理空值
     */
    private RetrievalResult buildCollectRetrieval(){
        // 创建检索结果对象，用于存储检索上下文和相关引用
        RetrievalResult rr = new RetrievalResult(); 
        // 创建字符串构建器，用于拼接检索上下文
        StringBuilder sb = new StringBuilder();
        // 创建分页对象，查询第1页，每页5条数据
        var page = new Page<Subject>(1, 5);
        // 调用subjectMapper的按收藏数排序查询方法，获取收藏数排行靠前的条目
        var res = subjectMapper.listByCollectCount(page);
        // 创建ChatRefSubject列表，用于存储条目引用数据
        java.util.ArrayList<ChatRefSubject> srefs = new java.util.ArrayList<>();
        // 遍历条目列表，构建上下文和引用数据
        for(Subject s: res.getRecords()){
            // 拼接条目信息到上下文字符串：条目名称、收藏数、平均评分
            sb.append("- 条目：").append(nvl(s.getCnName())).append(" 收藏数：").append(s.getCollectCount()==null?0:s.getCollectCount()).append(" 平均评分：").append(s.getAvgRating()==null?"":String.format("%.1f", s.getAvgRating())).append("\n");
            // 创建条目引用对象，设置属性并添加到列表
            ChatRefSubject rs = new ChatRefSubject(); 
            rs.id=s.getId(); 
            rs.cnName=s.getCnName(); 
            rs.jpName=s.getJpName(); 
            rs.alias=s.getAlias(); 
            rs.coverPath=s.getCoverPath(); 
            rs.collectCount=s.getCollectCount(); 
            rs.avgRating=s.getAvgRating(); 
            srefs.add(rs);
        }
        // 设置条目引用列表到检索结果
        rr.subjects = srefs; 
        // 设置上下文字符串到检索结果
        rr.context = sb.toString(); 
        // 返回检索结果
        return rr;
    }

    /*
     * 构建评分排行检索结果
     * 功能：获取评分最高的条目数据，构建AI上下文
     * 参数：
     *   - minCount：最小评分人次，用于过滤评分样本量不足的条目
     * 返回值：
     *   - RetrievalResult：包含评分排行上下文和相关条目引用数据
     * 调用关系：
     *   - subjectMapper.listByAvgRating：调用SubjectMapper接口的按平均评分排序查询方法
     *   - nvl：调用本类的nvl方法，处理空值
     */
    private RetrievalResult buildRatingRetrieval(int minCount){
        // 创建检索结果对象，用于存储检索上下文和相关引用
        RetrievalResult rr = new RetrievalResult(); 
        // 创建字符串构建器，用于拼接检索上下文
        StringBuilder sb = new StringBuilder();
        // 创建分页对象，查询第1页，每页5条数据
        var page = new Page<Subject>(1, 5);
        // 调用subjectMapper的按平均评分排序查询方法，获取评分最高的条目
        var res = subjectMapper.listByAvgRating(page, minCount);
        // 创建ChatRefSubject列表，用于存储条目引用数据
        java.util.ArrayList<ChatRefSubject> srefs = new java.util.ArrayList<>();
        // 遍历条目列表，构建上下文和引用数据
        for(Subject s: res.getRecords()){
            // 拼接条目信息到上下文字符串：条目名称、平均评分、评分人次
            sb.append("- 条目：").append(nvl(s.getCnName())).append(" 平均评分：").append(s.getAvgRating()==null?"":String.format("%.1f", s.getAvgRating())).append(" 评分人次：").append(s.getRatingCount()==null?0:s.getRatingCount()).append("\n");
            // 创建条目引用对象，设置属性并添加到列表
            ChatRefSubject rs = new ChatRefSubject(); 
            rs.id=s.getId(); 
            rs.cnName=s.getCnName(); 
            rs.jpName=s.getJpName(); 
            rs.alias=s.getAlias(); 
            rs.coverPath=s.getCoverPath(); 
            rs.avgRating=s.getAvgRating(); 
            srefs.add(rs);
        }
        // 设置条目引用列表到检索结果
        rr.subjects = srefs; 
        // 设置上下文字符串到检索结果
        rr.context = sb.toString(); 
        // 返回检索结果
        return rr;
    }
}
