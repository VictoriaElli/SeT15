window.message = function(text, type){
  const out = document.getElementById("feedback");
  out.textContent = text;
  out.className = type === "error" ? "alert error" : "alert success";
  setTimeout(()=>out.textContent="",3000);
};
