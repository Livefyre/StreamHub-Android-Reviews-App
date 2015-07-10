/*
 * Copyright (C) 2013 Manuel Peinado
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package livefyre.fadingactionbar;
import android.app.Activity;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import livefyre.R;

public class ListViewActivity extends Activity {
    private ArrayAdapter<String> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        FadingActionBarHelper helper = new FadingActionBarHelper()
            .actionBarBackground(getResources().getDrawable(
   				 R.color.colorPrimary))
            .headerLayout(R.layout.activity_parent_row)
            .contentLayout(R.layout.activity_review_indetail);
        setContentView(helper.createView(this));
        helper.initActionBar(this);

        ListView listView = (ListView) findViewById(R.id.commentsLV);
//        ArrayList<String> items = loadItems(R.raw.nyc_sites);
//        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, items);
//        listView.setAdapter(adapter);
    }

    /**
     * @return A list of Strings read from the specified resource
     */
//    private ArrayList<String> loadItems(int rawResourceId) {
//        try {
//            ArrayList<String> countries = new ArrayList<String>();
//            InputStream inputStream = getResources().openRawResource(rawResourceId);
//            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
//            String line;
//            while ((line = reader.readLine()) != null) {
//                countries.add(line);
//            }
//            reader.close();
//            return countries;
//        } catch (IOException e) {
//            return null;
//        }
//    }
//
//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        getMenuInflater().inflate(R.menu.listview_activity_menu, menu);
//        return true;
//    }
//
//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        if (item.getItemId() == R.id.action_change_dataset) {
//            changeDataset();
//            return true;
//        }
//        return super.onOptionsItemSelected(item);
//    }
//
//    private void changeDataset() {
//      adapter.notifyDataSetChanged();
//  }
}
