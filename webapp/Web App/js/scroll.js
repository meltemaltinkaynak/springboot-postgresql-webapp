document.addEventListener("DOMContentLoaded", () => {
  const arrow = document.getElementById("scrollArrow");
  let targetIndex = 0;

  const targets = document.querySelectorAll("#latest"); 

  if (arrow && targets.length > 0) {
    arrow.addEventListener("click", () => {
      if (targets[targetIndex]) {
        const target = targets[targetIndex];

        
        const targetPosition = target.getBoundingClientRect().top + window.pageYOffset;

        window.scrollTo({
          top: targetPosition - 80, 
          behavior: "smooth" 
        });

        // Bir sonraki hedefe geç
        targetIndex = (targetIndex + 1) % targets.length; 
      }
    });
  }
});
