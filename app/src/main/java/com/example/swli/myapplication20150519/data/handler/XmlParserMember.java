package com.example.swli.myapplication20150519.data.handler;

import com.example.swli.myapplication20150519.model.Member;

import org.xmlpull.v1.XmlPullParser;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import lsw.library.DateExt;

/**
 * Created by lsw_wsl on 7/18/15.
 */
public class XmlParserMember extends XmlParserData<List<Member>> {

    public XmlParserMember(InputStream inputStream) {
        super(inputStream);
    }

    @Override
    public List<Member> getT() {
        return memberList;
    }

    private String tempName;
    private String tempGender;
    private String tempBirthday;

    private List<Member> memberList;

    @Override
    public void startDocument(XmlPullParser parser) {
        memberList = new ArrayList<Member>();
    }

    @Override
    public void startTag(XmlPullParser parser) {
        if(parser.getName().equals("Member")){
            tempName = parser.getAttributeValue("", "Name");
            tempGender = parser.getAttributeValue("", "Gender");
            tempBirthday = parser.getAttributeValue("", "Birthday");
        }
    }

    @Override
    public void endTag(XmlPullParser parser) {
        if(parser.getName().equals("Member")){
            Member member = new Member();
            member.setBirthday(new DateExt(tempBirthday));
            member.setIsMale(tempGender.equals("男"));
            member.setName(tempName);
            memberList.add(member);
            tempName = tempGender = tempBirthday = "";
        }
    }

}
