package de.unipotsdam.elis.model.impl;

import com.liferay.portal.kernel.bean.AutoEscapeBeanHandler;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.json.JSON;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.ProxyUtil;
import com.liferay.portal.kernel.util.StringBundler;
import com.liferay.portal.kernel.util.StringPool;
import com.liferay.portal.model.CacheModel;
import com.liferay.portal.model.impl.BaseModelImpl;
import com.liferay.portal.service.ServiceContext;
import com.liferay.portal.util.PortalUtil;

import com.liferay.portlet.expando.model.ExpandoBridge;
import com.liferay.portlet.expando.util.ExpandoBridgeFactoryUtil;

import de.unipotsdam.elis.model.Evidence;
import de.unipotsdam.elis.model.EvidenceModel;
import de.unipotsdam.elis.model.EvidenceSoap;

import java.io.Serializable;

import java.sql.Types;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * The base model implementation for the Evidence service. Represents a row in the &quot;UPServices_Evidence&quot; database table, with each column mapped to a property of this class.
 *
 * <p>
 * This implementation and its corresponding interface {@link de.unipotsdam.elis.model.EvidenceModel} exist only as a container for the default property accessors generated by ServiceBuilder. Helper methods and all application logic should be put in {@link EvidenceImpl}.
 * </p>
 *
 * @author Brian Wing Shun Chan
 * @see EvidenceImpl
 * @see de.unipotsdam.elis.model.Evidence
 * @see de.unipotsdam.elis.model.EvidenceModel
 * @generated
 */
@JSON(strict = true)
public class EvidenceModelImpl extends BaseModelImpl<Evidence>
    implements EvidenceModel {
    /*
     * NOTE FOR DEVELOPERS:
     *
     * Never modify or reference this class directly. All methods that expect a evidence model instance should use the {@link de.unipotsdam.elis.model.Evidence} interface instead.
     */
    public static final String TABLE_NAME = "UPServices_Evidence";
    public static final Object[][] TABLE_COLUMNS = {
            { "evidenceId", Types.BIGINT },
            { "groupId", Types.BIGINT },
            { "companyId", Types.BIGINT },
            { "userId", Types.BIGINT },
            { "userName", Types.VARCHAR },
            { "createDate", Types.TIMESTAMP },
            { "modifiedDate", Types.TIMESTAMP },
            { "title", Types.VARCHAR },
            { "link", Types.VARCHAR },
            { "summary", Types.VARCHAR },
            { "activityTyp", Types.VARCHAR }
        };
    public static final String TABLE_SQL_CREATE = "create table UPServices_Evidence (evidenceId LONG not null primary key,groupId LONG,companyId LONG,userId LONG,userName VARCHAR(75) null,createDate DATE null,modifiedDate DATE null,title VARCHAR(900) null,link VARCHAR(900) null,summary VARCHAR(900) null,activityTyp VARCHAR(75) null)";
    public static final String TABLE_SQL_DROP = "drop table UPServices_Evidence";
    public static final String ORDER_BY_JPQL = " ORDER BY evidence.evidenceId ASC";
    public static final String ORDER_BY_SQL = " ORDER BY UPServices_Evidence.evidenceId ASC";
    public static final String DATA_SOURCE = "liferayDataSource";
    public static final String SESSION_FACTORY = "liferaySessionFactory";
    public static final String TX_MANAGER = "liferayTransactionManager";
    public static final boolean ENTITY_CACHE_ENABLED = GetterUtil.getBoolean(com.liferay.util.service.ServiceProps.get(
                "value.object.entity.cache.enabled.de.unipotsdam.elis.model.Evidence"),
            true);
    public static final boolean FINDER_CACHE_ENABLED = GetterUtil.getBoolean(com.liferay.util.service.ServiceProps.get(
                "value.object.finder.cache.enabled.de.unipotsdam.elis.model.Evidence"),
            true);
    public static final boolean COLUMN_BITMASK_ENABLED = false;
    public static final long LOCK_EXPIRATION_TIME = GetterUtil.getLong(com.liferay.util.service.ServiceProps.get(
                "lock.expiration.time.de.unipotsdam.elis.model.Evidence"));
    private static ClassLoader _classLoader = Evidence.class.getClassLoader();
    private static Class<?>[] _escapedModelInterfaces = new Class[] {
            Evidence.class
        };
    private long _evidenceId;
    private long _groupId;
    private long _companyId;
    private long _userId;
    private String _userUuid;
    private String _userName;
    private Date _createDate;
    private Date _modifiedDate;
    private String _title;
    private String _link;
    private String _summary;
    private String _activityTyp;
    private Evidence _escapedModel;

    public EvidenceModelImpl() {
    }

    /**
     * Converts the soap model instance into a normal model instance.
     *
     * @param soapModel the soap model instance to convert
     * @return the normal model instance
     */
    public static Evidence toModel(EvidenceSoap soapModel) {
        if (soapModel == null) {
            return null;
        }

        Evidence model = new EvidenceImpl();

        model.setEvidenceId(soapModel.getEvidenceId());
        model.setGroupId(soapModel.getGroupId());
        model.setCompanyId(soapModel.getCompanyId());
        model.setUserId(soapModel.getUserId());
        model.setUserName(soapModel.getUserName());
        model.setCreateDate(soapModel.getCreateDate());
        model.setModifiedDate(soapModel.getModifiedDate());
        model.setTitle(soapModel.getTitle());
        model.setLink(soapModel.getLink());
        model.setSummary(soapModel.getSummary());
        model.setActivityTyp(soapModel.getActivityTyp());

        return model;
    }

    /**
     * Converts the soap model instances into normal model instances.
     *
     * @param soapModels the soap model instances to convert
     * @return the normal model instances
     */
    public static List<Evidence> toModels(EvidenceSoap[] soapModels) {
        if (soapModels == null) {
            return null;
        }

        List<Evidence> models = new ArrayList<Evidence>(soapModels.length);

        for (EvidenceSoap soapModel : soapModels) {
            models.add(toModel(soapModel));
        }

        return models;
    }

    @Override
    public long getPrimaryKey() {
        return _evidenceId;
    }

    @Override
    public void setPrimaryKey(long primaryKey) {
        setEvidenceId(primaryKey);
    }

    @Override
    public Serializable getPrimaryKeyObj() {
        return _evidenceId;
    }

    @Override
    public void setPrimaryKeyObj(Serializable primaryKeyObj) {
        setPrimaryKey(((Long) primaryKeyObj).longValue());
    }

    @Override
    public Class<?> getModelClass() {
        return Evidence.class;
    }

    @Override
    public String getModelClassName() {
        return Evidence.class.getName();
    }

    @Override
    public Map<String, Object> getModelAttributes() {
        Map<String, Object> attributes = new HashMap<String, Object>();

        attributes.put("evidenceId", getEvidenceId());
        attributes.put("groupId", getGroupId());
        attributes.put("companyId", getCompanyId());
        attributes.put("userId", getUserId());
        attributes.put("userName", getUserName());
        attributes.put("createDate", getCreateDate());
        attributes.put("modifiedDate", getModifiedDate());
        attributes.put("title", getTitle());
        attributes.put("link", getLink());
        attributes.put("summary", getSummary());
        attributes.put("activityTyp", getActivityTyp());

        return attributes;
    }

    @Override
    public void setModelAttributes(Map<String, Object> attributes) {
        Long evidenceId = (Long) attributes.get("evidenceId");

        if (evidenceId != null) {
            setEvidenceId(evidenceId);
        }

        Long groupId = (Long) attributes.get("groupId");

        if (groupId != null) {
            setGroupId(groupId);
        }

        Long companyId = (Long) attributes.get("companyId");

        if (companyId != null) {
            setCompanyId(companyId);
        }

        Long userId = (Long) attributes.get("userId");

        if (userId != null) {
            setUserId(userId);
        }

        String userName = (String) attributes.get("userName");

        if (userName != null) {
            setUserName(userName);
        }

        Date createDate = (Date) attributes.get("createDate");

        if (createDate != null) {
            setCreateDate(createDate);
        }

        Date modifiedDate = (Date) attributes.get("modifiedDate");

        if (modifiedDate != null) {
            setModifiedDate(modifiedDate);
        }

        String title = (String) attributes.get("title");

        if (title != null) {
            setTitle(title);
        }

        String link = (String) attributes.get("link");

        if (link != null) {
            setLink(link);
        }

        String summary = (String) attributes.get("summary");

        if (summary != null) {
            setSummary(summary);
        }

        String activityTyp = (String) attributes.get("activityTyp");

        if (activityTyp != null) {
            setActivityTyp(activityTyp);
        }
    }

    @JSON
    @Override
    public long getEvidenceId() {
        return _evidenceId;
    }

    @Override
    public void setEvidenceId(long evidenceId) {
        _evidenceId = evidenceId;
    }

    @JSON
    @Override
    public long getGroupId() {
        return _groupId;
    }

    @Override
    public void setGroupId(long groupId) {
        _groupId = groupId;
    }

    @JSON
    @Override
    public long getCompanyId() {
        return _companyId;
    }

    @Override
    public void setCompanyId(long companyId) {
        _companyId = companyId;
    }

    @JSON
    @Override
    public long getUserId() {
        return _userId;
    }

    @Override
    public void setUserId(long userId) {
        _userId = userId;
    }

    @Override
    public String getUserUuid() throws SystemException {
        return PortalUtil.getUserValue(getUserId(), "uuid", _userUuid);
    }

    @Override
    public void setUserUuid(String userUuid) {
        _userUuid = userUuid;
    }

    @JSON
    @Override
    public String getUserName() {
        if (_userName == null) {
            return StringPool.BLANK;
        } else {
            return _userName;
        }
    }

    @Override
    public void setUserName(String userName) {
        _userName = userName;
    }

    @JSON
    @Override
    public Date getCreateDate() {
        return _createDate;
    }

    @Override
    public void setCreateDate(Date createDate) {
        _createDate = createDate;
    }

    @JSON
    @Override
    public Date getModifiedDate() {
        return _modifiedDate;
    }

    @Override
    public void setModifiedDate(Date modifiedDate) {
        _modifiedDate = modifiedDate;
    }

    @JSON
    @Override
    public String getTitle() {
        if (_title == null) {
            return StringPool.BLANK;
        } else {
            return _title;
        }
    }

    @Override
    public void setTitle(String title) {
        _title = title;
    }

    @JSON
    @Override
    public String getLink() {
        if (_link == null) {
            return StringPool.BLANK;
        } else {
            return _link;
        }
    }

    @Override
    public void setLink(String link) {
        _link = link;
    }

    @JSON
    @Override
    public String getSummary() {
        if (_summary == null) {
            return StringPool.BLANK;
        } else {
            return _summary;
        }
    }

    @Override
    public void setSummary(String summary) {
        _summary = summary;
    }

    @JSON
    @Override
    public String getActivityTyp() {
        if (_activityTyp == null) {
            return StringPool.BLANK;
        } else {
            return _activityTyp;
        }
    }

    @Override
    public void setActivityTyp(String activityTyp) {
        _activityTyp = activityTyp;
    }

    @Override
    public ExpandoBridge getExpandoBridge() {
        return ExpandoBridgeFactoryUtil.getExpandoBridge(getCompanyId(),
            Evidence.class.getName(), getPrimaryKey());
    }

    @Override
    public void setExpandoBridgeAttributes(ServiceContext serviceContext) {
        ExpandoBridge expandoBridge = getExpandoBridge();

        expandoBridge.setAttributes(serviceContext);
    }

    @Override
    public Evidence toEscapedModel() {
        if (_escapedModel == null) {
            _escapedModel = (Evidence) ProxyUtil.newProxyInstance(_classLoader,
                    _escapedModelInterfaces, new AutoEscapeBeanHandler(this));
        }

        return _escapedModel;
    }

    @Override
    public Object clone() {
        EvidenceImpl evidenceImpl = new EvidenceImpl();

        evidenceImpl.setEvidenceId(getEvidenceId());
        evidenceImpl.setGroupId(getGroupId());
        evidenceImpl.setCompanyId(getCompanyId());
        evidenceImpl.setUserId(getUserId());
        evidenceImpl.setUserName(getUserName());
        evidenceImpl.setCreateDate(getCreateDate());
        evidenceImpl.setModifiedDate(getModifiedDate());
        evidenceImpl.setTitle(getTitle());
        evidenceImpl.setLink(getLink());
        evidenceImpl.setSummary(getSummary());
        evidenceImpl.setActivityTyp(getActivityTyp());

        evidenceImpl.resetOriginalValues();

        return evidenceImpl;
    }

    @Override
    public int compareTo(Evidence evidence) {
        long primaryKey = evidence.getPrimaryKey();

        if (getPrimaryKey() < primaryKey) {
            return -1;
        } else if (getPrimaryKey() > primaryKey) {
            return 1;
        } else {
            return 0;
        }
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }

        if (!(obj instanceof Evidence)) {
            return false;
        }

        Evidence evidence = (Evidence) obj;

        long primaryKey = evidence.getPrimaryKey();

        if (getPrimaryKey() == primaryKey) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    public int hashCode() {
        return (int) getPrimaryKey();
    }

    @Override
    public void resetOriginalValues() {
    }

    @Override
    public CacheModel<Evidence> toCacheModel() {
        EvidenceCacheModel evidenceCacheModel = new EvidenceCacheModel();

        evidenceCacheModel.evidenceId = getEvidenceId();

        evidenceCacheModel.groupId = getGroupId();

        evidenceCacheModel.companyId = getCompanyId();

        evidenceCacheModel.userId = getUserId();

        evidenceCacheModel.userName = getUserName();

        String userName = evidenceCacheModel.userName;

        if ((userName != null) && (userName.length() == 0)) {
            evidenceCacheModel.userName = null;
        }

        Date createDate = getCreateDate();

        if (createDate != null) {
            evidenceCacheModel.createDate = createDate.getTime();
        } else {
            evidenceCacheModel.createDate = Long.MIN_VALUE;
        }

        Date modifiedDate = getModifiedDate();

        if (modifiedDate != null) {
            evidenceCacheModel.modifiedDate = modifiedDate.getTime();
        } else {
            evidenceCacheModel.modifiedDate = Long.MIN_VALUE;
        }

        evidenceCacheModel.title = getTitle();

        String title = evidenceCacheModel.title;

        if ((title != null) && (title.length() == 0)) {
            evidenceCacheModel.title = null;
        }

        evidenceCacheModel.link = getLink();

        String link = evidenceCacheModel.link;

        if ((link != null) && (link.length() == 0)) {
            evidenceCacheModel.link = null;
        }

        evidenceCacheModel.summary = getSummary();

        String summary = evidenceCacheModel.summary;

        if ((summary != null) && (summary.length() == 0)) {
            evidenceCacheModel.summary = null;
        }

        evidenceCacheModel.activityTyp = getActivityTyp();

        String activityTyp = evidenceCacheModel.activityTyp;

        if ((activityTyp != null) && (activityTyp.length() == 0)) {
            evidenceCacheModel.activityTyp = null;
        }

        return evidenceCacheModel;
    }

    @Override
    public String toString() {
        StringBundler sb = new StringBundler(23);

        sb.append("{evidenceId=");
        sb.append(getEvidenceId());
        sb.append(", groupId=");
        sb.append(getGroupId());
        sb.append(", companyId=");
        sb.append(getCompanyId());
        sb.append(", userId=");
        sb.append(getUserId());
        sb.append(", userName=");
        sb.append(getUserName());
        sb.append(", createDate=");
        sb.append(getCreateDate());
        sb.append(", modifiedDate=");
        sb.append(getModifiedDate());
        sb.append(", title=");
        sb.append(getTitle());
        sb.append(", link=");
        sb.append(getLink());
        sb.append(", summary=");
        sb.append(getSummary());
        sb.append(", activityTyp=");
        sb.append(getActivityTyp());
        sb.append("}");

        return sb.toString();
    }

    @Override
    public String toXmlString() {
        StringBundler sb = new StringBundler(37);

        sb.append("<model><model-name>");
        sb.append("de.unipotsdam.elis.model.Evidence");
        sb.append("</model-name>");

        sb.append(
            "<column><column-name>evidenceId</column-name><column-value><![CDATA[");
        sb.append(getEvidenceId());
        sb.append("]]></column-value></column>");
        sb.append(
            "<column><column-name>groupId</column-name><column-value><![CDATA[");
        sb.append(getGroupId());
        sb.append("]]></column-value></column>");
        sb.append(
            "<column><column-name>companyId</column-name><column-value><![CDATA[");
        sb.append(getCompanyId());
        sb.append("]]></column-value></column>");
        sb.append(
            "<column><column-name>userId</column-name><column-value><![CDATA[");
        sb.append(getUserId());
        sb.append("]]></column-value></column>");
        sb.append(
            "<column><column-name>userName</column-name><column-value><![CDATA[");
        sb.append(getUserName());
        sb.append("]]></column-value></column>");
        sb.append(
            "<column><column-name>createDate</column-name><column-value><![CDATA[");
        sb.append(getCreateDate());
        sb.append("]]></column-value></column>");
        sb.append(
            "<column><column-name>modifiedDate</column-name><column-value><![CDATA[");
        sb.append(getModifiedDate());
        sb.append("]]></column-value></column>");
        sb.append(
            "<column><column-name>title</column-name><column-value><![CDATA[");
        sb.append(getTitle());
        sb.append("]]></column-value></column>");
        sb.append(
            "<column><column-name>link</column-name><column-value><![CDATA[");
        sb.append(getLink());
        sb.append("]]></column-value></column>");
        sb.append(
            "<column><column-name>summary</column-name><column-value><![CDATA[");
        sb.append(getSummary());
        sb.append("]]></column-value></column>");
        sb.append(
            "<column><column-name>activityTyp</column-name><column-value><![CDATA[");
        sb.append(getActivityTyp());
        sb.append("]]></column-value></column>");

        sb.append("</model>");

        return sb.toString();
    }
}