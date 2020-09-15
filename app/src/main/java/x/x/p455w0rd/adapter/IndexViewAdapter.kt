package x.x.p455w0rd.adapter

import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import x.x.p455w0rd.R
import x.x.p455w0rd.TimeUtils
import x.x.p455w0rd.beans.PasswordItem

class IndexViewAdapter(layoutRes: Int = R.layout.main_password_item) :
    BaseQuickAdapter<PasswordItem, BaseViewHolder>(layoutRes) {
    override fun convert(holder: BaseViewHolder, item: PasswordItem) {
        holder.setText(R.id.main_item_title, item.title)
        holder.setText(R.id.main_item_name, item.account)
        holder.setText(R.id.main_item_password, item.password)
        holder.setText(R.id.main_item_date, TimeUtils.getConciseTime(item.time, context))
    }
}