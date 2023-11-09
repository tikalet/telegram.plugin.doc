package tikale.telegram.plugin.doc.message;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import tikale.telegram.plugin.doc.entity.DocType;
import tikale.telegram.plugin.doc.entity.SendType;
import tikale.telegram.plugin.doc.entity.dto.BotGetDto;
import tikale.telegram.plugin.doc.entity.dto.MessageDto;
import tikale.telegram.plugin.doc.entity.dto.MessageResponseDto;
import tikale.telegram.plugin.doc.util.FileUtil;
import tikale.telegram.plugin.doc.util.HttpUtill;
import tikale.telegram.plugin.doc.util.StringUtil;

@Component
public class MessageJob {

    private static final Logger LOG = LoggerFactory.getLogger(MessageJob.class);

    private static final String ERROR_MESSAGE = "Unable to send data. See to log";

    @Autowired
    private HttpUtill httpUtill;

    @Autowired
    private FileUtil fileUtil;

    @Scheduled(fixedRateString = "${message.get.fixedRate}", initialDelayString = "${message.get.initialDelay}")
    public void execute() {
        try {
            run();
        } catch (Exception e) {
            LOG.error("Timer unable to work", e);
        }
    }

    private void run() {
        List<BotGetDto> responseDataList = httpUtill.get();

        if (responseDataList == null || responseDataList.isEmpty()) {
            return;
        }

        for (BotGetDto dto : responseDataList) {
            LOG.debug("income message = " + dto);

            MessageDto message = dto.getMessage();

            if (!checkMessage(message.getText())) {
                continue;
            }

            String chat = dto.getHook().getCondition().getChatName();
            processCommand(chat);
        }

    }

    private void processCommand(String chat) {
        MessageResponseDto message = new MessageResponseDto();
        message.setChatName(chat);
        message.setType(SendType.MARKDOWN);

        try {
            message.setMessage(fileUtil.load());
        } catch (Exception e) {
            LOG.error(StringUtil.ERROR_TEXT, e);
            message.setMessage(ERROR_MESSAGE);
        }

        httpUtill.sendText(message);
    }

    private boolean checkMessage(String checkedText) {
        return DocType.DOC.getText().equals(checkedText);
    }

}
