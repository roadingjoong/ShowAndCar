package com.joljak.showandcar_app

import android.content.Context
import android.widget.ArrayAdapter

class CategoryAdapter(context: Context, resource: Int, categories: Array<String>) : ArrayAdapter<String>(context, resource, categories)