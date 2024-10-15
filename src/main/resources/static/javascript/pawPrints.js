    let pawPrints = [];
    const pawPrintSpacing = 75;
    const animationDuration = 12000;
    const waveDelay = 1500;

    function createPawPrints() {
    const container = document.getElementById('paw-prints-container');
    container.innerHTML = '';
    pawPrints = [];

    const containerWidth = container.offsetWidth;
    const totalPawPrints = Math.floor(containerWidth / pawPrintSpacing);
    const excessSpace = containerWidth - (totalPawPrints * pawPrintSpacing);
    const leftPadding = excessSpace / 2;
    let rotateUp = true;

    for (let i = 0; i < totalPawPrints; i++) {
    const pawPrint = document.createElement('img');
    pawPrint.src = '/images/PawPrintCartoonStyleTransparent.png';
    pawPrint.className = 'paw-print';
    pawPrint.style.left = (leftPadding + i * pawPrintSpacing) + 'px';

    if(rotateUp) {
    pawPrint.classList.add('rotate-up');
} else {
    pawPrint.classList.add('rotate-down');
}

    rotateUp = !rotateUp;
    container.appendChild(pawPrint);
    pawPrints.push(pawPrint);
}
    animatePawPrints();
}

    function animatePawPrints() {
    pawPrints.forEach((pawPrint, index) => {
        const delay = index * waveDelay;

        pawPrint.style.animation = `fadeInOut ${animationDuration}ms linear ${delay}ms infinite`;
    });
}

    function onResize() {
    createPawPrints();
}

    window.addEventListener('resize', onResize);
    window.addEventListener('load', () => {
    createPawPrints();
    animatePawPrints();
});
