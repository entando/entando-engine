package org.entando.entando.aps.system.services.actionlog.model;

import org.entando.entando.ent.exception.EntException;
import org.entando.entando.aps.system.services.DtoBuilder;
import org.entando.entando.aps.system.services.activitystream.ISocialActivityStreamManager;
import org.entando.entando.aps.system.services.activitystream.model.ActivityStreamComment;
import org.entando.entando.aps.system.services.activitystream.model.ActivityStreamLikeInfo;
import org.entando.entando.ent.util.EntLogging.EntLogger;
import org.entando.entando.ent.util.EntLogging.EntLogFactory;

import java.util.ArrayList;
import java.util.List;

public class ActionLogRecordDtoBuilder extends DtoBuilder<ActionLogRecord, ActionLogRecordDto> {

    private final EntLogger logger = EntLogFactory.getSanitizedLogger(getClass());

    @Deprecated
    @Override
    public ActionLogRecordDto convert(ActionLogRecord entity) {
        return super.convert(entity);
    }

    @Deprecated
    @Override
    public List<ActionLogRecordDto> convert(List<ActionLogRecord> list) {
        return super.convert(list);
    }

    @Override
    protected ActionLogRecordDto toDto(ActionLogRecord src) {
        ActionLogRecordDto dto = new ActionLogRecordDto(src);
        return dto;
    }

    public ActionLogRecordDto toDto(ActionLogRecord src, List<ActivityStreamLikeInfo> actionLikeRecords, List<ActivityStreamComment> actionCommentRecords) {
        ActionLogRecordDto dto = new ActionLogRecordDto(src, actionLikeRecords, actionCommentRecords);
        return dto;
    }

    public List<ActionLogRecordDto> convert(List<ActionLogRecord> list, ISocialActivityStreamManager socialActivityStreamManager) {
        List<ActionLogRecordDto> out = new ArrayList<>();
        list.stream().forEach(i -> {
            try {
                out.add(toDto(i, socialActivityStreamManager.getActionLikeRecords(i.getId()), socialActivityStreamManager.getActionCommentRecords(i.getId())));
            } catch (EntException e) {
                logger.error("error converting list ",e);
            }
        });

        return out;
    }

}
