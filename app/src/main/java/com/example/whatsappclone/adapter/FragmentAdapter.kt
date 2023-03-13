package com.example.whatsappclone.adapter

import android.app.FragmentManager
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentPagerAdapter
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.example.whatsappclone.R
import com.example.whatsappclone.fragments.CallFragment
import com.example.whatsappclone.fragments.ChatsFragment
import com.example.whatsappclone.fragments.StatusFragment


class FragmentAdapter(fm: androidx.fragment.app.FragmentManager) : FragmentPagerAdapter(fm) {
    override fun getCount(): Int {
        return 3
    }

    override fun getItem(position: Int): Fragment {
        return when(position){
            0-> ChatsFragment()
            1-> StatusFragment()
            2-> CallFragment()
            else-> ChatsFragment()
        }
    }

    override fun getPageTitle(position: Int): CharSequence? {
        var title:String? = null
        if (position==0)
            title="Chat"
        if (position==1)
            title="Status"
        if (position==2)
            title="Call"
        return title
    }

}
