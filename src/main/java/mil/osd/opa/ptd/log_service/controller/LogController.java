package mil.osd.opa.ptd.log_service.controller;

import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * $Id: $
 *
 *
 * @author $Author: $
 * @version $Revision: $ $Date: $
 *
 * $Source: $
 *
 */

import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.SortedMap;
import java.util.TreeMap;

import javax.servlet.http.HttpServletRequest;

import mil.osd.opa.ptd.log_service.beans.ErrorLog;
import mil.osd.opa.ptd.log_service.beans.ErrorLogThreshold;
import mil.osd.opa.ptd.log_service.service.LogService;
import mil.osd.opa.ptd.log_service.service.LogThresholdService;
import mil.osd.opa.ptd.log_service.utils.WdlptEncode;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

@RestController
@RequestMapping("/")
public class LogController {

    @Autowired
    private LogThresholdService logThreshold;
    @Autowired
    private LogService log;

    // This is required to bind the Validation code into Spring(Fortify recognizes
    // this)
    final String[] DISALLOWED_FIELDS = new String[] { "" };

    @InitBinder
    public void initBinder(WebDataBinder binder) {
        binder.setDisallowedFields(DISALLOWED_FIELDS);
    }

    @RequestMapping(value = "/sysprops", method = RequestMethod.GET, produces = "application/json")
    @ResponseBody 
    public SortedMap<String, String> props() {
        try {
            java.util.Properties props = System.getProperties();
            @SuppressWarnings({ "unchecked", "rawtypes" })
            SortedMap<String, String> pmap = new TreeMap(props);
            return pmap;
        } catch (Exception e) {
            log.error("Error retieving properties", e);
            return null;
        }
    }

    @RequestMapping(value = "/ping", method = RequestMethod.GET, produces = "application/json")
    @ResponseBody 
    public String pong(HttpServletRequest request) {
        String res = null;
        try {
            res = "PONG: ";
            res = res + request.getSession().getMaxInactiveInterval();
        } catch (Exception e) {
            throw e;
        }
        return res;

    }

    @RequestMapping(value = "/log/{id}", method = RequestMethod.GET)
    @ResponseBody
    public ErrorLog getEventLogInJSON(@PathVariable Long id) {
        return log.getLogById(id);
    }

    /**
     * Lookup all data from the log table.
     * 
     * @param Long starting  
     *          starting at record
     * @param Long pageSize  
     *          how many records to return max
     * @return all ErrorLog objects in a page
     */
    @RequestMapping(value = "/getLogs", method = RequestMethod.POST, consumes = "application/json", produces = "application/json")
    @ResponseBody
    public List<ErrorLog> getLogs(@RequestBody Long starting, Long pageSize) {
      List<ErrorLog> errorLogs = null;
      errorLogs = log.getLogs(starting, pageSize); // logMapper.getLogs(WdlptEncode.forJava(systemTest));
      return errorLogs;
    }

    @RequestMapping(value = "/log", method = RequestMethod.POST, consumes = "application/json", produces = "application/json")
    @ResponseBody
    public void postEventLog(@RequestBody ErrorLog aLog) {
        try {
            log.log(aLog);

        } catch (Exception e) {
            throw e;
        }
    }

    @RequestMapping(value = "/log2", method = RequestMethod.POST, consumes = "application/json", produces = "application/json")
    @ResponseBody
    public void postEventLogInJSON(@RequestBody HashMap<String, String> input) {
        ErrorLog aLog = new ErrorLog();
        /* Example JSON input:
        {
            "logLvlCd": "DEBUG",
            "dlptErrMsgDtm": "2018-02-16T19:34:58.000+0000",
            "srcClsNm": "mil.osd.dmdc.ptd.ptp.LogService",
            "srcMthdNm": "insert",
            "dlptErrMsgCdTx": "0",
            "dlptErrMsgTx": null,
            "excThrwnTx": "Error reported on: http://forge.int.dmdc.osd.mil:10141/icat/appmonitor.statu was: java.lang.Exception",
            "hostNm": "longshot.int.dmdc.osd.mil"
        }
        */
        try {
            aLog = new ErrorLog().message(WdlptEncode.forSQL(input.get("dlptErrMsgTx")))
                    .level(WdlptEncode.forSQL(input.get("logLvlCd")))
                    .method(WdlptEncode.forSQL(input.get("srcMthdNm")))
                    .className(WdlptEncode.forSQL(input.get("srcClsNm")));

            String dlptErrMsgCdTx = WdlptEncode.forSQL(input.get("dlptErrMsgCdTx"));
            aLog.setDlptErrMsgCdTx(dlptErrMsgCdTx);
            // Timestamp assigned always by LogService as an Override
//            String dlptErrMsgDtm = WdlptEncode.forSQL(input.get("dlptErrMsgDtm"));
//            
//            Date date = null;
//            try {
//                DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS", Locale.ENGLISH);
//                date = (Date) formatter.parse(dlptErrMsgDtm);
//            } catch (ParseException pe) {
//              date = null;  
//            }
//            aLog.setDlptErrMsgDtm(date);
            String excThrwnTx = WdlptEncode.forSQL(input.get("excThrwnTx"));
            aLog.setExcThrwnTx(excThrwnTx);
            String hostNm = WdlptEncode.forSQL(input.get("hostNm"));
            aLog.setHostNm(hostNm);

            log.log(aLog);

        } catch (Exception e) {
            throw e;
        }
    }

    // Insert new LogThreshold
    @RequestMapping(value = "/loglevel/set/{thrshldClsNm}/{logLvlCd}/{tstSiteId}", method = RequestMethod.GET)
    @ResponseBody
    public ErrorLogThreshold setLogLevel(@PathVariable String thrshldClsNm, @PathVariable String logLvlCd,
            @PathVariable String tstSiteId) {

        ErrorLogThreshold thisLT = new ErrorLogThreshold();
        try {
            if (tstSiteId != null && !tstSiteId.equals("")) {
                // Remove surrounding quotes (field can be empty)
                tstSiteId = tstSiteId.substring(1, tstSiteId.length() - 1);
                if (tstSiteId.equals("")) { // if empty, it is NULL in DB
                    tstSiteId = null;
                }
            }

            // Check if threshold exists before insert
            thisLT = logThreshold.getThreshold(thrshldClsNm, tstSiteId);
            if (thisLT != null) {
                return null;
            }

            // insert values
            thisLT = new ErrorLogThreshold();
            thisLT.setThrshldClsNm(WdlptEncode.forHtmlContent(thrshldClsNm));
            thisLT.setLogLvlCd(WdlptEncode.forHtmlContent(logLvlCd));
            thisLT.setTstSiteId(WdlptEncode.forHtmlContent(tstSiteId));

            logThreshold.insertThreshold(thisLT); // setThreshold(thrshldClsNm, logLvlCd, tstSiteId);

            thisLT = logThreshold.getThreshold(thrshldClsNm, tstSiteId);

        } catch (Exception e) {
            log.error("Error inserting LogThreshold", e);
            throw e;
        }

        return thisLT;
    }

    @RequestMapping(value = "/loglevel/get/{thrshldClsNm}", method = RequestMethod.GET)
    @ResponseBody
    public String getLogLevel(@PathVariable String thrshldClsNm) {

        String level = null;

        try {

            level = logThreshold.getThresholdValue(thrshldClsNm, null); // No tstSiteId
            if (level == null)
                level = logThreshold.getBestMatch(thrshldClsNm.trim(), null); // No tstSiteId

        } catch (Exception e) {
            log.error("Error finding LogThreshold", e);
            throw e;
        }

        return level;
    }

    @RequestMapping(value = "/loglevel", method = RequestMethod.POST)
    @ResponseBody
    public ErrorLogThreshold setLoglevelInJSON(@Validated @RequestBody ErrorLogThreshold threshold) {

        ErrorLogThreshold returnThreshold = null;

        try {
            // Get next id value
            String logThrshldId = WdlptEncode.forHtmlAttribute(threshold.getLogThrshldId().toString());
            threshold.setLogThrshldId(new BigDecimal(logThrshldId));

            logThreshold.updateThreshold(threshold);
            returnThreshold = logThreshold.getThreshold(threshold.getThrshldClsNm(), threshold.getTstSiteId());
            // logThreshold.logChange(operator, returnThreshold); // future

        } catch (Exception e) {
            log.error("Error updating LogThreshold", e);
            throw e;
        }

        return returnThreshold;

    }

    @RequestMapping(value = "/loglevels", method = RequestMethod.GET)
    @ResponseBody
    public List<ErrorLogThreshold> getLogLevels() {

        List<ErrorLogThreshold> thresholds = null;

        try {
            thresholds = logThreshold.getThresholds();

        } catch (Exception e) {
            log.error("Error getting Log Threshold list", e);
            throw e;
        }

        return thresholds;
    }

    @RequestMapping(value = "/loglevel/refresh", method = RequestMethod.GET)
    @ResponseBody
    public void refreshLogLevel() {

        try {
            logThreshold.refreshThresholds();

        } catch (Exception e) {
            log.error("Error refreshing Thresholds", e);
        }
    }

}
