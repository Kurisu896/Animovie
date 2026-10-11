package com.bgm.controller;

import com.bgm.common.ApiResponse;
import com.bgm.domain.Subject;
import com.bgm.mapper.SubjectMapper;
import com.bgm.service.PageResult;
import com.bgm.service.SubjectService;
import com.bgm.utils.security.JwtUtil;
import io.jsonwebtoken.Claims;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.nio.file.Files.deleteIfExists;

@CrossOrigin
/*
 * 控制器：条目（subjects）
 * 职责：获取条目详情、封面上传、评分统计、列表查询/高级筛选等
 */
@RestController
@RequestMapping("/subject")
public class SubjectController {
    @Autowired
    SubjectService subjectService;
    @Autowired
    SubjectMapper subjectMapper;
    private boolean isAdmin(String token){
        if(token==null) return false;
        if(token.startsWith("Bearer ")) token=token.substring(7);
        Claims c = JwtUtil.parseClaims(token);
        if(c==null) return false;
        Object role=c.get("role");
        return role!=null && role.toString().equals("ADMIN");
    }
    @PostMapping
    public ApiResponse<Integer> add(@RequestHeader(value="Authorization",required=false) String token,@RequestBody Subject s){
        if(!isAdmin(token)) return ApiResponse.fail("forbidden");
        return ApiResponse.ok(subjectService.add(s));
    }
    @PutMapping
    public ApiResponse<Integer> edit(@RequestHeader(value="Authorization",required=false) String token,@RequestBody Subject s){
        if(!isAdmin(token)) return ApiResponse.fail("forbidden");
        return ApiResponse.ok(subjectService.edit(s));
    }
    @DeleteMapping("/{id}")
    public ApiResponse<Integer> remove(@RequestHeader(value="Authorization",required=false) String token,@PathVariable Long id){
        if(!isAdmin(token)) return ApiResponse.fail("forbidden");
        try{
            Subject s = subjectService.get(id);
            if(s!=null){
                String p = s.getCoverPath();
                if(p!=null && p.startsWith("/uploads/covers/")){
                    String fn = p.substring(p.lastIndexOf('/')+1);
                    if(!fn.isEmpty() && !fn.equalsIgnoreCase("default.jpg")){
                        java.nio.file.Path dir = java.nio.file.Paths.get("uploads","covers");
                        deleteIfExists(dir.resolve(fn));
                    }
                }
            }
        }catch(Exception ignore){}
        return ApiResponse.ok(subjectService.remove(id));
    }
    @PostMapping(value="/cover", consumes = org.springframework.http.MediaType.MULTIPART_FORM_DATA_VALUE)
    public ApiResponse<String> uploadCover(@RequestHeader(value="Authorization",required=false) String token,@RequestParam("file") org.springframework.web.multipart.MultipartFile file,@RequestParam(value="cnName",required=false) String cnName,@RequestParam(value="oldPath",required=false) String oldPath){
        if(!isAdmin(token)) return ApiResponse.fail("forbidden");
        try{
            java.nio.file.Path dir = java.nio.file.Paths.get("uploads","covers");
            java.nio.file.Files.createDirectories(dir);
            String orig = file.getOriginalFilename();
            String ext = "";
            if(orig!=null){
                int i = orig.lastIndexOf('.');
                if(i>=0){ ext = orig.substring(i); }
            }
            String base = cnName!=null && !cnName.trim().isEmpty() ? cnName.trim() : java.util.UUID.randomUUID().toString();
            base = base.replaceAll("[\\/:*?\"<>|]"," ").replaceAll("\\s+","_");
            String name;
            java.nio.file.Path target;
            if(oldPath!=null && !oldPath.trim().isEmpty()){
                String p = oldPath.trim();
                if(p.startsWith("/uploads/covers/")){
                    String fn = p.substring(p.lastIndexOf('/')+1);
                    if(!fn.isEmpty()){
                        java.nio.file.Path old = dir.resolve(fn);
                        if(!fn.equalsIgnoreCase("default.jpg")){
                            try{ deleteIfExists(old); }catch(Exception ignore){}
                        }
                    }
                }
                name = base + ext;
                target = dir.resolve(name);
            }else{
                name = base + ext;
                target = dir.resolve(name);
                int n=1;
                while(java.nio.file.Files.exists(target)){
                    name = base + "-" + n + ext;
                    target = dir.resolve(name);
                    n++;
                }
            }
            try(InputStream in = file.getInputStream()){
                Files.copy(in, target, StandardCopyOption.REPLACE_EXISTING);
            }
            String url = "/uploads/covers/"+name;
            return ApiResponse.ok(url);
        }catch (Exception e){
            return ApiResponse.fail("upload failed");
        }
    }
    @GetMapping
    public ApiResponse<PageResult<Subject>> list(@RequestParam(required=false) String keyword,
                                                 @RequestParam(required=false) String category,
                                                 @RequestParam(required=false) String country,
                                                 @RequestParam(required=false) Integer year,
                                                 @RequestParam(required=false) Integer month,
                                                 @RequestParam(required=false) Double ratingMin,
                                                 @RequestParam(required=false) Double ratingMax,
                                                 @RequestParam(required=false) String tagKeyword,
                                                 @RequestParam(defaultValue="1") int pageNum,
                                                 @RequestParam(defaultValue="10") int pageSize){
        return ApiResponse.ok(subjectService.listAdv(keyword,category,country,year,month,ratingMin,ratingMax,tagKeyword,pageNum,pageSize));
    }
    /** 获取条目详情 */
    @GetMapping("/{id}")
    public ApiResponse<Subject> get(@PathVariable Long id){
        return ApiResponse.ok(subjectService.get(id));
    }

    /** 评分统计：返回平均分与各分数分布 */
    @GetMapping("/rating-stats")
    public ApiResponse<Map<String,Object>> ratingStats(@RequestParam Long subjectId){
        Double avg = subjectMapper.avgRatingBySubject(subjectId);
        List<Map<String,Object>> rows = subjectMapper.ratingCountsBySubject(subjectId);
        int[] counts = new int[11];
        int total = 0;
        if(rows!=null){
            for(Map<String,Object> m : rows){
                Object rr = m.get("r"); Object cc = m.get("c");
                int r = rr==null?0: Integer.parseInt(rr.toString());
                int c = cc==null?0: Integer.parseInt(cc.toString());
                if(r>=0 && r<=10){ counts[r] = c; total += c; }
            }
        }
        Map<String,Object> data = new HashMap<>();
        data.put("avg", avg==null?0.0:avg);
        data.put("counts", counts);
        data.put("total", total);
        return ApiResponse.ok(data);
    }
}
