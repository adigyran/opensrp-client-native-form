package com.vijay.jsonwizard.widgets;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.rey.material.util.ViewUtil;
import com.vijay.jsonwizard.R;
import com.vijay.jsonwizard.constants.JsonFormConstants;
import com.vijay.jsonwizard.customviews.CheckBox;
import com.vijay.jsonwizard.fragments.JsonFormFragment;
import com.vijay.jsonwizard.interfaces.CommonListener;
import com.vijay.jsonwizard.interfaces.FormWidgetFactory;
import com.vijay.jsonwizard.interfaces.JsonApi;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by vijay on 24-05-2015.
 */
public class CheckBoxFactory implements FormWidgetFactory {
    @Override
    public List<View> getViewsFromJson(String stepName, Context context, JsonFormFragment formFragment, JSONObject jsonObject, CommonListener listener) throws Exception {
        List<View> views = new ArrayList<>(1);
        JSONArray canvasIds = new JSONArray();

        String openMrsEntityParent = jsonObject.getString("openmrs_entity_parent");
        String openMrsEntity = jsonObject.getString("openmrs_entity");
        String openMrsEntityId = jsonObject.getString("openmrs_entity_id");
        String relevance = jsonObject.optString("relevance");
        TextView textView = com.vijay.jsonwizard.utils.FormUtils.getTextViewWith(context, 27, jsonObject.getString("label"), jsonObject.getString(JsonFormConstants.KEY),
                jsonObject.getString("type"), openMrsEntityParent, openMrsEntity, openMrsEntityId,
                relevance,
                com.vijay.jsonwizard.utils.FormUtils.getLayoutParams(com.vijay.jsonwizard.utils.FormUtils.MATCH_PARENT, com.vijay.jsonwizard.utils.FormUtils.WRAP_CONTENT, 0, 0, 0, 0), com.vijay.jsonwizard.utils.FormUtils.FONT_BOLD_PATH);
        canvasIds.put(textView.getId());
        views.add(textView);

        boolean readOnly = false;
        if (jsonObject.has(JsonFormConstants.READ_ONLY)) {
            readOnly = jsonObject.getBoolean(JsonFormConstants.READ_ONLY);
        }

        JSONArray options = jsonObject.getJSONArray(JsonFormConstants.OPTIONS_FIELD_NAME);
        ArrayList<CheckBox> checkBoxes = new ArrayList<>();
        for (int i = 0; i < options.length(); i++) {
            JSONObject item = options.getJSONObject(i);
            LinearLayout checkboxLayout = (LinearLayout) LayoutInflater.from(context).inflate(R.layout.native_form_item_checkbox, null);
            TextView text1 = (TextView) checkboxLayout.findViewById(R.id.text1);
            text1.setText(item.getString(JsonFormConstants.TEXT));
            final CheckBox checkBox = (CheckBox) checkboxLayout.findViewById(R.id.checkbox);
            checkBoxes.add(checkBox);
            checkBox.setTag(R.id.raw_value, item.getString(JsonFormConstants.TEXT));
            checkBox.setTag(R.id.key, jsonObject.getString(JsonFormConstants.KEY));
            checkBox.setTag(R.id.type, jsonObject.getString("type"));
            checkBox.setTag(R.id.openmrs_entity_parent, openMrsEntityParent);
            checkBox.setTag(R.id.openmrs_entity, openMrsEntity);
            checkBox.setTag(R.id.openmrs_entity_id, openMrsEntityId);
            checkBox.setTag(R.id.childKey, item.getString(JsonFormConstants.KEY));
            checkBox.setTag(R.id.address, stepName + ":" + jsonObject.getString(JsonFormConstants.KEY));
            //checkBox.setTextSize(context.getResources().getDimension(R.dimen.default_text_size));
            checkBox.setOnCheckedChangeListener(listener);
            checkboxLayout.setClickable(true);
            checkboxLayout.setId(ViewUtil.generateViewId());
            canvasIds.put(checkboxLayout.getId());
            checkboxLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    checkBox.toggle();
                }
            });
            if (!TextUtils.isEmpty(item.optString(JsonFormConstants.VALUE))) {
                checkBox.setChecked(Boolean.valueOf(item.optString(JsonFormConstants.VALUE)));
            }
            checkBox.setEnabled(!readOnly);
            checkBox.setFocusable(!readOnly);
            if (i == options.length() - 1) {
                checkboxLayout.setLayoutParams(com.vijay.jsonwizard.utils.FormUtils.getLayoutParams(com.vijay.jsonwizard.utils.FormUtils.MATCH_PARENT, com.vijay.jsonwizard.utils.FormUtils.WRAP_CONTENT, 0, 0, 0, (int) context
                        .getResources().getDimension(R.dimen.extra_bottom_margin)));
            }

            views.add(checkboxLayout);
            ((JsonApi) context).addFormDataView(checkBox);

            if (relevance != null && context instanceof JsonApi) {
                checkBox.setTag(R.id.relevance, relevance);
                ((JsonApi) context).addSkipLogicView(checkBox);
            }

            String constraints = item.optString("constraints");
            if (constraints != null && context instanceof JsonApi) {
                checkBox.setTag(R.id.constraints, constraints);
                ((JsonApi) context).addConstrainedView(checkBox);
            }
        }

        for (CheckBox checkBox : checkBoxes) {
            checkBox.setTag(R.id.canvas_ids, canvasIds.toString());
        }
        return views;
    }
}
