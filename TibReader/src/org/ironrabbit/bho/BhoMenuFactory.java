
package org.ironrabbit.bho;

import java.util.ArrayList;
import java.util.List;

import org.ironrabbit.bho.BhoMenu.BhoMenuItem;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.widget.TableRow;

public class BhoMenuFactory {
	List<BhoMenuItem> items;
	int maxRows, itemsPerRow;
	List<TableRow> rows;
	Context c;

	interface BhoMenuBuilder {
		public void show(View v);
	}

	BhoMenuFactory(Context c) {
		this.c = c;
		items = new ArrayList<BhoMenuItem>();
		rows = new ArrayList<TableRow>();
	}

	void build() {
		int cols = 0;
		rows.add(initTableRow());

		for(BhoMenuItem item : items) {
			try {
				((TableRow) item.getParent()).removeView(item);
			} catch(NullPointerException e) {}
			if(cols % itemsPerRow == 0 && cols != 0) {
				rows.add(initTableRow());
				cols = 0;
			}

			TableRow tr = rows.get(rows.size() - 1);
			tr.addView(item);
			cols++;
		}

	}

	private TableRow initTableRow() {
		TableRow tr = new TableRow(c);
		tr.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.FILL_PARENT, TableRow.LayoutParams.WRAP_CONTENT));
		return tr;
	}

}
