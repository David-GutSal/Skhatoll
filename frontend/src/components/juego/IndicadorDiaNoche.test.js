import { mount } from '@vue/test-utils'
import { describe, it, expect, vi } from 'vitest'
import IndicadorDiaNoche from './IndicadorDiaNoche.vue'

describe('IndicadorDiaNoche', () => {
  it('debería renderizar correctamente en modo día', () => {
    const wrapper = mount(IndicadorDiaNoche, {
      props: { esDia: true },
    })

    expect(wrapper.find('.indicador-wrapper').classes()).toContain('dia')
    expect(wrapper.findAll('.btn-fase')).toHaveLength(2)
  })

  it('debería renderizar correctamente en modo noche', () => {
    const wrapper = mount(IndicadorDiaNoche, {
      props: { esDia: false },
    })

    expect(wrapper.find('.indicador-wrapper').classes()).toContain('noche')
  })

  it('debería marcar el botón correcto como activo en día', () => {
    const wrapper = mount(IndicadorDiaNoche, {
      props: { esDia: true },
    })

    const botones = wrapper.findAll('.btn-fase')
    expect(botones[0].classes()).toContain('activo')
    expect(botones[1].classes()).not.toContain('activo')
  })

  it('debería marcar el botón correcto como activo en noche', () => {
    const wrapper = mount(IndicadorDiaNoche, {
      props: { esDia: false },
    })

    const botones = wrapper.findAll('.btn-fase')
    expect(botones[0].classes()).not.toContain('activo')
    expect(botones[1].classes()).toContain('activo')
  })

  it('debería deshabilitar botones cuando hay votación activa', () => {
    const wrapper = mount(IndicadorDiaNoche, {
      props: { esDia: true, votacionActiva: true },
    })

    const botones = wrapper.findAll('.btn-fase')
    expect(botones[0].classes()).toContain('deshabilitado')
    expect(botones[1].classes()).toContain('deshabilitado')
  })

  it('debería deshabilitar el botón de la fase actual cuando no hay votación activa', () => {
    const wrapper = mount(IndicadorDiaNoche, {
      props: { esDia: true, votacionActiva: false },
    })

    const botones = wrapper.findAll('.btn-fase')
    expect(botones[0].classes()).toContain('deshabilitado')
    expect(botones[1].classes()).not.toContain('deshabilitado')
  })

  it('debería emitir evento cambiarFase al hacer click en fase opuesta', async () => {
    const wrapper = mount(IndicadorDiaNoche, {
      props: { esDia: true },
    })

    const botonNoche = wrapper.findAll('.btn-fase')[1]
    await botonNoche.trigger('click')

    expect(wrapper.emitted('cambiarFase')).toBeTruthy()
    expect(wrapper.emitted('cambiarFase')[0]).toEqual(['noche'])
  })

  it('debería permitir cambiar a día cuando es de noche', async () => {
    const wrapper = mount(IndicadorDiaNoche, {
      props: { esDia: false },
    })

    const botonDia = wrapper.findAll('.btn-fase')[0]
    await botonDia.trigger('click')

    expect(wrapper.emitted('cambiarFase')).toBeTruthy()
    expect(wrapper.emitted('cambiarFase')[0]).toEqual(['dia'])
  })

  it('no debería emitir evento cuando votacionActiva y se hace click', async () => {
    const wrapper = mount(IndicadorDiaNoche, {
      props: { esDia: true, votacionActiva: true },
    })

    const botonDia = wrapper.findAll('.btn-fase')[0]
    await botonDia.trigger('click')

    expect(wrapper.emitted('cambiarFase')).toBeFalsy()
  })

  it('debería tener los atributos aria correcto', () => {
    const wrapper = mount(IndicadorDiaNoche, {
      props: { esDia: true, votacionActiva: true },
    })

    const botones = wrapper.findAll('.btn-fase')
    expect(botones[0].attributes('aria-disabled')).toBe('true')
    expect(botones[1].attributes('aria-disabled')).toBe('true')
  })

  it('debería tener title correcto según estado', () => {
    const wrapperConVotacion = mount(IndicadorDiaNoche, {
      props: { esDia: true, votacionActiva: true },
    })

    const boton = wrapperConVotacion.find('.btn-fase')
    expect(boton.attributes('title')).toContain('Cierra la votación')
  })

  it('debería usar valores por defecto correctamente', () => {
    const wrapper = mount(IndicadorDiaNoche)

    expect(wrapper.props('esDia')).toBe(true)
    expect(wrapper.props('votacionActiva')).toBe(false)
  })
})