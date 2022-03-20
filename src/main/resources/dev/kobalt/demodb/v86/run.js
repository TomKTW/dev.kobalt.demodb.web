"use strict";
document.getElementById("startContainer").style = "display: none";
document.getElementById("demoContainer").style = "display: visible";

var entryImage = "$entryImage";
var systemImage = "$systemImage";
var mouse_is_enabled = true;
var os_uses_mouse = false;
var progress_ticks = 0;

function chr_repeat(chr, count) {
    var result = "";
    while (count-- > 0) { result += chr; }
    return result;
};

function show_progress(e) {
    var el = document.getElementById("loading");
    el.style.display = "block";

    if (e.file_index === e.file_count - 1 && e.loaded >= e.total - 2048) {
        /* last file is (almost) loaded */
        el.textContent = "Done downloading. Starting now ...";
        return;
    }

    var line = "Downloading images ";

    if (typeof e.file_index === "number" && e.file_count) {
        line += "[" + (e.file_index + 1) + "/" + e.file_count + "] ";
    }

    if (e.total && typeof e.loaded === "number") {
        var per100 = Math.floor(e.loaded / e.total * 100);
        per100 = Math.min(100, Math.max(0, per100));
        var per50 = Math.floor(per100 / 2);
        line += per100 + "% [";
        line += chr_repeat("#", per50);
        line += chr_repeat(" ", 50 - per50) + "]";
    } else {
        line += chr_repeat(".", progress_ticks++ % 50);
    }

    el.textContent = line;
};

document.getElementById("screen_container").style.display = "none";
var emulator = window.emulator = new V86Starter({
    memory_size: 32 * 1024 * 1024,
    vga_memory_size: 2 * 1024 * 1024,
    screen_container: document.getElementById("screen_container"),
    bios: { url: "../../demo/v86/seabios.bin", },
    vga_bios: { url: "../../demo/v86/vgabios.bin", },
    fda: { "url": entryImage, "size": 1474560 },
    hda: { "url": systemImage },
    boot_order: 0x132,
    autostart: true
});

emulator.add_listener("emulator-started", function() {
    document.getElementById("run").value = "Halt";;
    document.getElementById("screen_container").style.display = "block";
    document.getElementById("loading").style.display = "none";
});

emulator.add_listener("download-progress", function(e) {
    show_progress(e);
});

emulator.add_listener("download-error", function(e) {
    var el = document.getElementById("loading");
    el.style.display = "block";
    el.textContent = "Loading " + e.file_name + " failed. Check your connection " +
        "and reload the page to try again.";
});

document.getElementById("run").onclick = function() {
    if (emulator.is_running()) {
        this.value = "Run";
        emulator.stop();
    } else {
        this.value = "Halt";
        emulator.run();
    }
    this.blur();
};

document.getElementById("reset").onclick = function() {
    emulator.restart();
    this.blur();
};

document.getElementById("ctrlaltdel").onclick = function() {
    emulator.keyboard_send_scancodes([
        0x1D, /* ctrl */
        0x38, /* alt */
        0x53, /* delete */

        /* break codes */
        0x1D | 0x80,
        0x38 | 0x80,
        0x53 | 0x80,
    ]);
    this.blur();
};

document.getElementById("fullscreen").onclick = function() {
    emulator.screen_go_fullscreen();
};

document.getElementById("screen_container").onclick = function() {
    if (mouse_is_enabled && os_uses_mouse) {
        emulator.lock_mouse();
    }
};

document.getElementById("scale").onchange = function() {
    var n = parseFloat(this.value);

    if (n || n > 0) {
        emulator.screen_set_scale(n, n);
    }
};

document.getElementById("stretch").onchange = function() {
    if (this.checked) {
        document.getElementById("screen_container").style.width = "100%";
        document.getElementById("vga").style.width = "100%";
        document.getElementById("screen_container").style.height = "100%";
        document.getElementById("vga").style.height = "100%";
    } else {
        document.getElementById("screen_container").style.width = "";
        document.getElementById("vga").style.width = "";
        document.getElementById("screen_container").style.width = "";
        document.getElementById("vga").style.width = "";
    }
};