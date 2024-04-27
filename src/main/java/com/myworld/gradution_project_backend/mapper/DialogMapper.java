package com.myworld.gradution_project_backend.mapper;

import com.myworld.gradution_project_backend.VO.FullDialog;
import com.myworld.gradution_project_backend.bean.Dialog;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
* @author wkz
* @description 针对表【dialog(对话记录表)】的数据库操作Mapper
* @createDate 2024-03-27 15:27:38
* @Entity com.myworld.gradution_project_backend.bean.Dialog
*/
public interface DialogMapper extends BaseMapper<Dialog> {
    int getDialogCountByUserId(@Param("userId") String userId);
    List<FullDialog> getDialogHistoryByUserIdAndSessionSeq(@Param("id") String userId,
                                                           @Param("sessionSeq") Integer sessionSeq);
}




