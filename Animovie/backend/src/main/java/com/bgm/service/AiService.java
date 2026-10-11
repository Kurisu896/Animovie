package com.bgm.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

/*
 * AI服务类
 * 功能：处理AI聊天相关的业务逻辑，调用外部AI API获取回答
 * 注解：@Service - 标记为Spring服务组件
 * 依赖：
 *   - Spring Value注解：从配置文件或环境变量中读取AI API配置
 *   - java.net.http包：用于发送HTTP请求
 *   - Jackson库：用于解析JSON响应
 * 调用关系：
 *   - 被AiController.chat方法调用，接收用户提问并返回AI回答
 */
@Service
public class AiService {
    // API密钥，用于API身份验证
    @Value("${ai.dashscope.api.key:${DASHSCOPE_API_KEY:}}")
    private String apiKey;
    
    // AI模型名称
    @Value("${ai.dashscope.model:qwen-plus}")
    private String model;
    
    // AI API基础URL,API请求地址
    @Value("${ai.dashscope.base-url:https://dashscope.aliyuncs.com/compatible-mode/v1}")
    private String baseUrl;

    /*
     * 官网api使用样板
     * 功能：接收用户消息，调用AI API获取回答
     * 参数：
     *   - message：用户提问消息
     * 返回值：
     *   - String：AI生成的回答
     * 异常：
     *   - Exception：网络请求或API调用异常
     * 调用关系：
     *   - 被AiController.chat方法调用
     */
    public String chat(String message) throws Exception {
        // 检查消息是否为空，若为空则直接返回空字符串
        if(message==null || message.trim().isEmpty()) return "";
        
        // 构建AI API请求体，包含模型、系统提示和用户消息
        String payload = "{\n"+
                "  \"model\": \""+model+"\",\n"+
                "  \"messages\": [\n"+
                "    { \"role\": \"system\", \"content\": \"你是本站 AI 助手。请根据给定的信息回答问题，优先围绕站内内容与功能回答，" +
                "                                           不要引用外部站点或猜测，如有剧情问题可使用外站数据简述。\" },\n"+
                "    { \"role\": \"user\", \"content\": " + escapeJson(message) + " }\n"+
                "  ]\n"+
                "}";

        // 构建HTTP请求，包含URL、授权头、内容类型和请求体
        HttpRequest req = HttpRequest.newBuilder()
                .uri(URI.create(baseUrl+"/chat/completions")) // 构建完整的API请求地址
                .header("Authorization", "Bearer " + apiKey) // 添加授权头
                .header("Content-Type", "application/json") // 设置内容类型为JSON
                .POST(HttpRequest.BodyPublishers.ofString(payload)) // 使用POST方法发送请求
                .build();
        
        // 创建HTTP客户端
        HttpClient client = HttpClient.newHttpClient();
        
        // 发送HTTP请求并获取响应
        HttpResponse<String> resp = client.send(req, HttpResponse.BodyHandlers.ofString());
        
        // 获取响应体
        String body = resp.body();
        
        // 从响应体中提取AI回答内容
        String content = extractContent(body);
        
        // 返回AI回答，若提取失败则返回空字符串
        return content!=null?content:"";
    }

    /**
     * JSON字符串转义方法
     * 功能：将普通字符串转换为JSON格式的字符串
     * 参数：
     *   - s：需要转义的字符串
     * 返回值：
     *   - String：转义后的JSON字符串
     * 调用关系：
     *   - 被本类chat方法调用，用于处理用户消息
     */
    private String escapeJson(String s){
        // 转义JSON特殊字符：反斜杠、双引号、换行符
        String t = s.replace("\\", "\\\\").replace("\"", "\\\"").replace("\n", "\\n");
        // 添加字符串引号，返回完整的JSON字符串格式
        return "\""+t+"\"";
    }

    /**
     * 提取AI回答内容方法
     * 功能：从AI API返回的JSON响应中提取回答内容
     * 参数：
     *   - body：AI API返回的JSON响应体
     * 返回值：
     *   - String：提取出的AI回答内容，若提取失败则返回null
     * 调用关系：
     *   - 被本类chat方法调用，用于解析API响应
     */
    private String extractContent(String body){
        try{
            // 创建Jackson ObjectMapper实例，用于解析JSON
            com.fasterxml.jackson.databind.ObjectMapper m = new com.fasterxml.jackson.databind.ObjectMapper();
            
            // 将JSON字符串解析为JsonNode对象
            com.fasterxml.jackson.databind.JsonNode root = m.readTree(body);
            
            // 获取choices字段
            com.fasterxml.jackson.databind.JsonNode choices = root.path("choices");
            
            // 检查choices是否为数组且有元素
            if(choices.isArray() && choices.size()>0){
                // 获取第一个choice中的message字段
                com.fasterxml.jackson.databind.JsonNode msg = choices.get(0).path("message");
                
                // 获取message中的content字段
                com.fasterxml.jackson.databind.JsonNode c = msg.path("content");
                
                // 检查content是否为文本类型，若是则返回文本内容
                if(c.isTextual()) return c.asText();
            }
        }
        // 捕获所有异常，避免因为JSON解析错误导致服务崩溃
        catch(Exception ignored){}
        
        // 若提取失败则返回null
        return null;
    }
}
