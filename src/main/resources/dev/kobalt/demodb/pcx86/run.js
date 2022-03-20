document.getElementById("startContainer").style = "display: none";
document.getElementById("demoContainer").style = "display: visible";

embedPCx86(
    "screen_container",
    "../../demo/pcx86/configuration.xml",
    "../../demo/pcx86/components.xsl",
    '{"messages":"warn","autoMount":{"A":{"path":"$entryImage"}}, floppyDrives:[{boot:false},{}], drives:[{name:"10Mb Hard Disk",type:1,path:"$systemImage"}]}'
);