// Imágenes y colores de cada rol — las descripciones vienen del backend

import brujaImg     from '@/assets/imgs/carta-bruja.jpg'
import aldeanoImg   from '@/assets/imgs/carta-aldeano.jpg'
import cazadorImg   from '@/assets/imgs/carta-cazador.jpg'
import loboImg      from '@/assets/imgs/carta-lobo.jpg'
import videnteImg   from '@/assets/imgs/carta-vidente.jpg'
import ninnaImg     from '@/assets/imgs/carta-ninna.jpg'
import ninnoImg     from '@/assets/imgs/carta-ninno.jpg'
import cupidoImg    from '@/assets/imgs/carta-cupido.jpg'

// ── MAPA DE IMÁGENES ──────────────────────────────────────────
export const IMAGENES_ROL = {
  BRUJA:          brujaImg,
  ALDEANO:        aldeanoImg,
  CAZADOR:        cazadorImg,
  LOBO:           loboImg,
  VIDENTE:        videnteImg,
  NIÑA:           ninnaImg,
  'NIÑO SALVAJE': ninnoImg,
  CUPIDO:         cupidoImg,
}

// ── COLORES POR BANDO ─────────────────────────────────────────
export const COLORES_BANDO = {
  lobo:     '#8b0000',
  aldea:    '#2d5a1b',
  narrador: '#1a3a5c',
}

// ── HELPERS ───────────────────────────────────────────────────
export function getImagenRol(nombreRol) {
  if (!nombreRol) return brujaImg
  return IMAGENES_ROL[nombreRol.toUpperCase()] || brujaImg
}

export function getColorBando(bando) {
  if (!bando) return '#555'
  return COLORES_BANDO[bando.toLowerCase()] || '#555'
}

// ── FRASES ROTATIVAS ─────────────────────────────────────────
export const FRASES = [
  'Cuando las barbas de tu vecino veas cortar, pon las tuyas a remojar.',
  'El lobo ya no come la carne que quiere, sino la que puede.',
  'Cuidado con el lobo con piel de cordero.',
  'Las apariencias engañan: la criatura más dulce puede ser la más diabólica.',
  'En Castronegro, nadie es lo que parece al caer la noche.',
  'La verdad es un lujo que pocos pueden permitirse en la aldea.',
  'El silencio de un inocente puede costar más vidas que la mentira de un culpable.',
  'Confiar es un regalo que en Castronegro muy pocos se pueden permitir.',
  'Pueblo dividido, victoria de los lobos.',
  'Quien mucho acusa, algo oculta.',
  'En la noche todos los aldeanos parecen lobos.',
  'Más vale aldeano desconfiado que lobo suelto.',
  'El lobo callado es el más peligroso.',
  'En este pueblo hay más lobos que ovejas.',
  'Aquí huele a perro mojado…',
  'Cuando el tonto coge la linde, la linde se acaba pero el tonto sigue'
]