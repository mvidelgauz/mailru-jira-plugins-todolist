/*
 * Created by Andrey Markelov 13-02-2013.
 * Copyright Mail.Ru Group 2013. All rights reserved.
 */
package ru.mail.jira.plugins;

import java.util.LinkedHashSet;
import java.util.Map;
import com.atlassian.jira.issue.Issue;
import com.atlassian.jira.issue.customfields.impl.AbstractSingleFieldType;
import com.atlassian.jira.issue.customfields.impl.FieldValidationException;
import com.atlassian.jira.issue.customfields.manager.GenericConfigManager;
import com.atlassian.jira.issue.customfields.persistence.CustomFieldValuePersister;
import com.atlassian.jira.issue.customfields.persistence.PersistenceFieldType;
import com.atlassian.jira.issue.fields.CustomField;
import com.atlassian.jira.issue.fields.layout.field.FieldLayoutItem;
import com.atlassian.jira.util.NotNull;
import com.atlassian.jira.util.json.JSONArray;
import com.atlassian.jira.util.json.JSONException;
import com.atlassian.jira.util.json.JSONObject;
import com.atlassian.util.concurrent.Nullable;

/**
 * ToDo list custom field.
 * 
 * @author Andrey Markelov
 */
public class ToDoListCf
    extends AbstractSingleFieldType<String>
{
    /**
     * Constructor.
     */
    protected ToDoListCf(
        CustomFieldValuePersister customFieldValuePersister,
        GenericConfigManager genericConfigManager)
    {
        super(customFieldValuePersister, genericConfigManager);
    }

    @Override
    public String getChangelogValue(
        CustomField field,
        String value)
    {
        return super.getChangelogValue(field, value);
    }

    @Override
    @NotNull
    protected PersistenceFieldType getDatabaseType()
    {
        return PersistenceFieldType.TYPE_UNLIMITED_TEXT;
    }

    @Override
    @Nullable
    protected Object getDbValueFromObject(String str)
    {
        return str;
    }

    @Override
    @Nullable
    protected String getObjectFromDbValue(
        @NotNull Object obj)
    throws FieldValidationException
    {
        return (null == obj) ? "" : obj.toString();
    }

    @Override
    public String getSingularObjectFromString(String str)
    throws FieldValidationException
    {
        return str;
    }

    @Override
    public String getStringFromSingularObject(String str)
    {
        return str;
    }

    @Override
    @NotNull
    public Map getVelocityParameters(
        Issue issue,
        CustomField field,
        FieldLayoutItem fieldLayoutItem)
    {
        Map<String, Object> params = super.getVelocityParameters(issue, field, fieldLayoutItem);

        LinkedHashSet<ToDoItem> items = new LinkedHashSet<ToDoItem>();
        Object value = issue.getCustomFieldValue(field);
        if (value != null && !value.toString().isEmpty())
        {
            try
            {
                JSONArray jsonObj = new JSONArray(value.toString());
                for (int i = 0; i < jsonObj.length(); i++)
                {
                    JSONObject obj = jsonObj.getJSONObject(i);
                    String todo = obj.getString("id");
                    String type = obj.getString("type");

                    if (type.equals("done"))
                    {
                        items.add(new ToDoItem(todo, true));
                    }
                    else
                    {
                        items.add(new ToDoItem(todo, false));
                    }
                }
            }
            catch (JSONException e)
            {
                e.printStackTrace();
            }
            params.put("data", value.toString());
        }
        else
        {
            params.put("data", "[]");
        }
        params.put("i18n", getI18nBean());
        params.put("items", items);
        params.put("currtime", Long.valueOf(System.currentTimeMillis()).toString());

        return params;
    }
}
