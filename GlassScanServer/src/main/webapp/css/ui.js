function show_signup(){
    $(".login").hide();
    $(".tablogin").removeClass("current");
    $(".signup").show();
    $(".signup").css('display','table');
    $(".tabsignup").addClass("current");
}

function show_login(){
    $(".signup").hide();
    $(".tabsignup").removeClass("current");
    $(".login").show();
    $(".login").css('display','table');
    $(".tablogin").addClass("current");
}

function select_all_scripts(checked){
    if(checked){
        $("input:checkbox.step1_scriptselect_checkbox").prop("checked", true);
    } else {
        $("input:checkbox.step1_scriptselect_checkbox").prop("checked", false);
    }
}