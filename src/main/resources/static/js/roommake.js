function roomcontrol() {
  let pcount = document.getElementById("player").value;
  if (pcount > 4) {
    alert("人数は4人までです");
    return false;
  } else if (bc < 1) {
    alert("1人以下はは入力できません");
    return false;
  } else {
    return true;
  }
}
